package kr.pickple.back.member.mapper;

import java.util.List;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.MemberStatus;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.position.domain.Position;

public final class MemberMapper {

    public static Member mapToMemberEntity(final NewMember newMember, final MainAddress mainAddress) {
        return Member.builder()
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

    public static List<MemberPosition> mapToMemberPositionEntities(
            final List<Position> positions,
            final Long memberId
    ) {
        return positions.stream()
                .map(position -> MemberPosition.builder()
                        .memberId(memberId)
                        .position(position)
                        .build()
                ).toList();
    }

    public static MemberProfile mapToMemberProfileDomain(
            final Member member,
            final MainAddress mainAddress,
            final List<Position> positions
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
                .build();
    }
}
