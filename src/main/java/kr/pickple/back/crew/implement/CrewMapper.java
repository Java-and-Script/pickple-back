package kr.pickple.back.crew.implement;

import java.util.List;

import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.CrewProfile;
import kr.pickple.back.crew.domain.NewCrew;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CrewMapper {

    public static CrewEntity mapNewCrewDomainToEntity(final NewCrew newCrew, final MainAddress mainAddress) {
        return CrewEntity.builder()
                .name(newCrew.getName())
                .content(newCrew.getContent())
                .maxMemberCount(newCrew.getMaxMemberCount())
                .leaderId(newCrew.getLeader().getMemberId())
                .profileImageUrl(newCrew.getProfileImageUrl())
                .backgroundImageUrl(newCrew.getBackgroundImageUrl())
                .addressDepth1Id(mainAddress.getAddressDepth1Id())
                .addressDepth2Id(mainAddress.getAddressDepth2Id())
                .chatRoomId(newCrew.getChatRoom().getChatRoomId())
                .build();
    }

    public static Crew mapCrewEntityToDomain(
            final CrewEntity crewEntity,
            final MainAddress mainAddress,
            final Member leader
    ) {
        return Crew.builder()
                .crewId(crewEntity.getId())
                .name(crewEntity.getName())
                .content(crewEntity.getContent())
                .memberCount(crewEntity.getMemberCount())
                .maxMemberCount(crewEntity.getMaxMemberCount())
                .leader(leader)
                .addressDepth1Name(mainAddress.getAddressDepth1Name())
                .addressDepth2Name(mainAddress.getAddressDepth2Name())
                .profileImageUrl(crewEntity.getProfileImageUrl())
                .backgroundImageUrl(crewEntity.getBackgroundImageUrl())
                .likeCount(crewEntity.getLikeCount())
                .competitionPoint(crewEntity.getCompetitionPoint())
                .build();
    }

    public static CrewMemberEntity mapCrewMemberDomainToEntity(final CrewMember crewMember) {
        return CrewMemberEntity.builder()
                .status(crewMember.getStatus())
                .memberId(crewMember.getMember().getMemberId())
                .crewId(crewMember.getCrew().getCrewId())
                .build();
    }

    public static CrewMember mapCrewMemberEntityToDomain(
            final CrewMemberEntity crewMemberEntity,
            final Member member,
            final Crew crew
    ) {
        return CrewMember.builder()
                .crewMemberId(crewMemberEntity.getId())
                .status(crewMemberEntity.getStatus())
                .member(member)
                .crew(crew)
                .build();
    }

    public static CrewProfile mapCrewEntityToCrewProfile(
            final CrewEntity crewEntity,
            final MainAddress mainAddress,
            final Member leader,
            final List<Member> members
    ) {
        return CrewProfile.builder()
                .crewId(crewEntity.getId())
                .name(crewEntity.getName())
                .content(crewEntity.getContent())
                .memberCount(crewEntity.getMemberCount())
                .maxMemberCount(crewEntity.getMaxMemberCount())
                .profileImageUrl(crewEntity.getProfileImageUrl())
                .backgroundImageUrl(crewEntity.getBackgroundImageUrl())
                .status(crewEntity.getStatus())
                .likeCount(crewEntity.getLikeCount())
                .competitionPoint(crewEntity.getCompetitionPoint())
                .leader(leader)
                .addressDepth1(mainAddress.getAddressDepth1Name())
                .addressDepth2(mainAddress.getAddressDepth2Name())
                .members(members)
                .build();
    }
}
