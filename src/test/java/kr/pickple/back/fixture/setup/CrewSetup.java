package kr.pickple.back.fixture.setup;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.fixture.domain.CrewFixtures;
import kr.pickple.back.member.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static kr.pickple.back.common.domain.RegistrationStatus.CONFIRMED;

@Component
public class CrewSetup {

    @Autowired
    private CrewRepository crewRepository;

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

        crew.addCrewMember(leader);
        savedChatRoom.updateMaxMemberCount(crew.getMaxMemberCount());
        crew.makeNewCrewChatRoom(savedChatRoom);

        final CrewMember crewLeader = crew.getCrewMembers().get(0);
        leader.addMemberCrew(crewLeader);
        crewLeader.updateStatus(CONFIRMED);

        return crewRepository.save(crew);
    }

    public Crew saveWithWaitingMembers(final Integer memberCount) {
        final List<Member> members = memberSetup.save(memberCount);
        final Crew crew = save(members.get(0));
        final List<Member> crewMembers = members.subList(1, members.size());

        crewMembers.forEach(crew::addCrewMember);

        return crew;
    }

    public Crew saveWithConfirmedMembers(final Integer memberCount) {
        final Crew crew = saveWithWaitingMembers(memberCount);
        final Member crewLeader = crew.getLeader();
        final List<CrewMember> crewMembers = crew.getCrewMembers();

        crewMembers.forEach(crewMember -> {
            if (!crewLeader.equals(crewMember.getMember())) {
                crewMember.updateStatus(CONFIRMED);
            }
        });

        return crew;
    }
}
