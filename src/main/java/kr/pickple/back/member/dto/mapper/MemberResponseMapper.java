package kr.pickple.back.member.dto.mapper;

import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;

public final class MemberResponseMapper {

    public static AuthenticatedMemberResponse mapToAuthenticatedMemberResponseDto(final NewMember newMember) {
        return AuthenticatedMemberResponse.builder()
                .accessToken(newMember.getAuthTokens().getAccessToken())
                .refreshToken(newMember.getAuthTokens().getRefreshToken())
                .id(newMember.getMemberId())
                .nickname(newMember.getNickname())
                .profileImageUrl(newMember.getProfileImageUrl())
                .email(newMember.getEmail())
                .oauthId(newMember.getOauthId())
                .oauthProvider(newMember.getOauthProvider())
                .addressDepth1(newMember.getAddressDepth1Name())
                .addressDepth2(newMember.getAddressDepth2Name())
                .build();
    }

    public static MemberProfileResponse mapToMemberProfileResponseDto(final MemberProfile memberProfile) {
        return MemberProfileResponse.builder()
                .id(memberProfile.getMemberId())
                .email(memberProfile.getEmail())
                .nickname(memberProfile.getNickname())
                .introduction(memberProfile.getIntroduction())
                .profileImageUrl(memberProfile.getProfileImageUrl())
                .mannerScore(memberProfile.getMannerScore())
                .mannerScoreCount(memberProfile.getMannerScoreCount())
                .addressDepth1(memberProfile.getAddressDepth1Name())
                .addressDepth2(memberProfile.getAddressDepth2Name())
                .positions(memberProfile.getPositions())
                .crews(memberProfile.getJoinedCrews())
                .build();
    }
}
