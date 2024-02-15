package kr.pickple.back.crew.implement;

import org.springframework.stereotype.Component;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewDomain;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.CrewMemberDomain;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrewMapper {

    private final AddressReader addressReader;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    public Crew mapCrewDomainToEntity(final CrewDomain crew) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(
                crew.getAddressDepth1Name(),
                crew.getAddressDepth2Name()
        );

        return Crew.builder()
                .name(crew.getName())
                .content(crew.getContent())
                .maxMemberCount(crew.getMaxMemberCount())
                .leaderId(crew.getLeader().getId())
                .profileImageUrl(crew.getProfileImageUrl())
                .backgroundImageUrl(crew.getBackgroundImageUrl())
                .addressDepth1Id(mainAddress.getAddressDepth1().getId())
                .addressDepth2Id(mainAddress.getAddressDepth2().getId())
                .chatRoomId(crew.getChatRoom().getId())
                .build();
    }

    public CrewDomain mapCrewEntityToDomain(final Crew crewEntity) {
        final MainAddress mainAddress = addressReader.readMainAddressById(
                crewEntity.getAddressDepth1Id(),
                crewEntity.getAddressDepth2Id()
        );

        return CrewDomain.builder()
                .crewId(crewEntity.getId())
                .name(crewEntity.getName())
                .content(crewEntity.getContent())
                .memberCount(crewEntity.getMemberCount())
                .maxMemberCount(crewEntity.getMaxMemberCount())
                .leader(memberRepository.getMemberById(crewEntity.getLeaderId()))
                .addressDepth1Name(mainAddress.getAddressDepth1().getName())
                .addressDepth2Name(mainAddress.getAddressDepth2().getName())
                .profileImageUrl(crewEntity.getProfileImageUrl())
                .backgroundImageUrl(crewEntity.getBackgroundImageUrl())
                .likeCount(crewEntity.getLikeCount())
                .competitionPoint(crewEntity.getCompetitionPoint())
                .chatRoom(chatRoomRepository.getChatRoomById(crewEntity.getChatRoomId()))
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
