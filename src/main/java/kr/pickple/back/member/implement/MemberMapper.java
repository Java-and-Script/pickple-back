package kr.pickple.back.member.implement;

import java.util.List;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.member.repository.entity.MemberEntity;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.repository.entity.MemberPositionEntity;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.MemberStatus;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberMapper {

    public static MemberEntity mapToMemberEntity(final NewMember newMember, final MainAddress mainAddress) {
        return MemberEntity.builder()
                .email(newMember.getEmail())
                .nickname(newMember.getNickname())
                .profileImageUrl(newMember.getProfileImageUrl())
                .status(MemberStatus.ACTIVE)
                .oauthId(newMember.getOauthId())
                .oauthProvider(newMember.getOauthProvider())
                .addressDepth1Id(mainAddress.getAddressDepth1().getId())
                .addressDepth2Id(mainAddress.getAddressDepth2().getId())
                .build();
    }

    public static List<MemberPositionEntity> mapToMemberPositionEntities(
            final List<Position> positions,
            final Long memberId
    ) {
        return positions.stream()
                .map(position -> MemberPositionEntity.builder()
                        .memberId(memberId)
                        .position(position)
                        .build()
                ).toList();
    }

    public static MemberProfile mapToMemberProfileDomain(
            final MemberEntity member,
            final MainAddress mainAddress,
            final List<Position> positions,
            final List<Crew> crews
    ) {

        return MemberProfile.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .mannerScore(member.getMannerScore())
                .mannerScoreCount(member.getMannerScoreCount())
                .addressDepth1Name(mainAddress.getAddressDepth1().getName())
                .addressDepth2Name(mainAddress.getAddressDepth2().getName())
                .positions(positions)
                .joinedCrews(crews)
                .build();
    }

    public static Member mapToMemberDomain(
            final MemberEntity memberEntity,
            final MainAddress mainAddress,
            final List<Position> positions
    ) {
        return Member.builder()
                .memberId(memberEntity.getId())
                .email(memberEntity.getEmail())
                .nickname(memberEntity.getNickname())
                .introduction(memberEntity.getIntroduction())
                .profileImageUrl(memberEntity.getProfileImageUrl())
                .mannerScore(memberEntity.getMannerScore())
                .mannerScoreCount(memberEntity.getMannerScoreCount())
                .addressDepth1Name(mainAddress.getAddressDepth1().getName())
                .addressDepth2Name(mainAddress.getAddressDepth2().getName())
                .positions(positions)
                .build();
    }
}
