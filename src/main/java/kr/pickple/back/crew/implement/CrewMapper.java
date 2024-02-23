package kr.pickple.back.crew.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewDomain;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.CrewMemberDomain;
import kr.pickple.back.crew.domain.NewCrew;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrewMapper {

    private final AddressReader addressReader;
    private final MemberReader memberReader;
    private final CrewMemberRepository crewMemberRepository;
    private final ChatRoomRepository chatRoomRepository;

    public Crew mapNewCrewDomainToEntity(final NewCrew newCrew) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(
                newCrew.getAddressDepth1Name(),
                newCrew.getAddressDepth2Name()
        );

        return Crew.builder()
                .name(newCrew.getName())
                .content(newCrew.getContent())
                .maxMemberCount(newCrew.getMaxMemberCount())
                .leaderId(newCrew.getLeader().getMemberId())
                .profileImageUrl(newCrew.getProfileImageUrl())
                .backgroundImageUrl(newCrew.getBackgroundImageUrl())
                .addressDepth1Id(mainAddress.getAddressDepth1().getId())
                .addressDepth2Id(mainAddress.getAddressDepth2().getId())
                .chatRoomId(newCrew.getChatRoom().getId())
                .build();
    }

    public CrewDomain mapCrewEntityToDomain(final Crew crewEntity) {
        final MainAddress mainAddress = addressReader.readMainAddressById(
                crewEntity.getAddressDepth1Id(),
                crewEntity.getAddressDepth2Id()
        );

        final List<MemberDomain> members = crewMemberRepository.findAllByCrewIdAndStatus(crewEntity.getId(), CONFIRMED)
                .stream()
                .map(crewMember -> memberReader.readByMemberId(crewMember.getMemberId()))
                .toList();

        return CrewDomain.builder()
                .crewId(crewEntity.getId())
                .name(crewEntity.getName())
                .content(crewEntity.getContent())
                .memberCount(crewEntity.getMemberCount())
                .maxMemberCount(crewEntity.getMaxMemberCount())
                .leader(memberReader.readByMemberId(crewEntity.getLeaderId()))
                .addressDepth1Name(mainAddress.getAddressDepth1().getName())
                .addressDepth2Name(mainAddress.getAddressDepth2().getName())
                .profileImageUrl(crewEntity.getProfileImageUrl())
                .backgroundImageUrl(crewEntity.getBackgroundImageUrl())
                .likeCount(crewEntity.getLikeCount())
                .competitionPoint(crewEntity.getCompetitionPoint())
                .chatRoom(chatRoomRepository.getChatRoomById(crewEntity.getChatRoomId()))
                .members(members)
                .build();
    }

    public CrewMember mapCrewMemberDomainToEntity(final CrewMemberDomain crewMember) {
        return CrewMember.builder()
                .status(crewMember.getStatus())
                .memberId(crewMember.getMemberId())
                .crewId(crewMember.getCrewId())
                .build();
    }
}
