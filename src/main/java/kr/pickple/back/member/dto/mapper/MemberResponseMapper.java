package kr.pickple.back.member.dto.mapper;

import java.util.List;

import kr.pickple.back.crew.dto.mapper.CrewResponseMapper;
import kr.pickple.back.crew.dto.response.CrewResponse;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
        final List<CrewResponse> joinedCrewResponses = memberProfile.getJoinedCrews()
                .stream()
                .map(CrewResponseMapper::mapToCrewResponseDto)
                .toList();

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
                .crews(joinedCrewResponses)
                .build();
    }

    public static MemberResponse mapToMemberResponseDto(final MemberDomain member) {
        return MemberResponse.of(member);
    }
}
