package kr.pickple.back.fixture.setup;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.repository.entity.ChatRoomEntity;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.fixture.domain.CrewFixtures;
import kr.pickple.back.member.domain.Member;

@Component
public class CrewSetup {

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberSetup memberSetup;

    @Autowired
    private AddressSetup addressSetup;

    public CrewEntity save(final Member leader) {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final CrewEntity crew = CrewFixtures.crewBuild(addressDepth1, addressDepth2, leader);
        final ChatRoomEntity savedChatRoom = chatRoomRepository.save(CrewFixtures.crewChatRoomBuild());

        final CrewMemberEntity crewLeader = CrewFixtures.crewMemberBuild(leader, crew);
        crewLeader.confirmRegistration();
        leader.addMemberCrew(crewLeader);

        savedChatRoom.updateMaxMemberCount(crew.getMaxMemberCount());
        crew.makeNewCrewChatRoom(savedChatRoom);

        final CrewEntity savedCrew = crewRepository.save(crew);
        crewMemberRepository.save(crewLeader);

        return savedCrew;
    }

    public CrewEntity saveWithWaitingMembers(final Integer memberCount) {
        final List<Member> members = memberSetup.save(memberCount);
        final CrewEntity crew = save(members.get(0));

        members.subList(1, members.size())
                .stream()
                .map(member -> CrewFixtures.crewMemberBuild(member, crew))
                .forEach(crewMemberRepository::save);

        return crew;
    }

    public CrewEntity saveWithConfirmedMembers(final Integer memberCount) {
        final CrewEntity crew = saveWithWaitingMembers(memberCount);

        crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), WAITING)
                .forEach(waitingCrewMember -> waitingCrewMember.updateStatus(CONFIRMED));

        return crew;
    }
}
