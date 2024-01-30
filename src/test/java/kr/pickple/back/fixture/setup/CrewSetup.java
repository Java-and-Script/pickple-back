package kr.pickple.back.fixture.setup;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
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

    public Crew save(final Member leader) {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final Crew crew = CrewFixtures.crewBuild(addressDepth1, addressDepth2, leader);
        final ChatRoom savedChatRoom = chatRoomRepository.save(CrewFixtures.crewChatRoomBuild());

        final CrewMember crewLeader = CrewFixtures.crewMemberBuild(leader, crew);
        crewLeader.confirmRegistration();
        leader.addMemberCrew(crewLeader);

        savedChatRoom.updateMaxMemberCount(crew.getMaxMemberCount());
        crew.makeNewCrewChatRoom(savedChatRoom);

        final Crew savedCrew = crewRepository.save(crew);
        crewMemberRepository.save(crewLeader);

        return savedCrew;
    }

    public Crew saveWithWaitingMembers(final Integer memberCount) {
        final List<Member> members = memberSetup.save(memberCount);
        final Crew crew = save(members.get(0));

        members.subList(1, members.size())
                .stream()
                .map(member -> CrewFixtures.crewMemberBuild(member, crew))
                .forEach(crewMemberRepository::save);

        return crew;
    }

    public Crew saveWithConfirmedMembers(final Integer memberCount) {
        final Crew crew = saveWithWaitingMembers(memberCount);

        crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), WAITING)
                .forEach(waitingCrewMember -> waitingCrewMember.updateStatus(CONFIRMED));

        return crew;
    }
}
