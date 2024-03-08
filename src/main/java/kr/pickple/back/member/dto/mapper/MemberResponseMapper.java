package kr.pickple.back.member.dto.mapper;

import java.util.List;

import kr.pickple.back.crew.dto.mapper.CrewResponseMapper;
import kr.pickple.back.crew.dto.response.CrewResponse;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberGameResponse;
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

    public static MemberResponse mapToMemberResponseDto(final Member member) {
        return MemberResponse.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .profileImageUrl(member.getProfileImageUrl())
                .mannerScore(member.getMannerScore())
                .mannerScoreCount(member.getMannerScoreCount())
                .addressDepth1(member.getAddressDepth1Name())
                .addressDepth2(member.getAddressDepth2Name())
                .positions(member.getPositions())
                .build();
    }

    public static List<MemberResponse> mapToMemberResponseDtos(final List<Member> members) {
        return members.stream()
                .map(MemberResponseMapper::mapToMemberResponseDto)
                .toList();
    }

    public static MemberGameResponse mapToMemberGameResponseDto(
            final Game game,
            final List<Member> members,
            final Boolean isReviewDone
    ) {
        final List<MemberResponse> memberResponses = members.stream()
                .map(MemberResponseMapper::mapToMemberResponseDto)
                .toList();

        return MemberGameResponse.builder()
                .id(game.getGameId())
                .content(game.getContent())
                .playDate(game.getPlayDate())
                .playStartTime(game.getPlayStartTime())
                .playEndTime(game.getPlayEndTime())
                .playTimeMinutes(game.getPlayTimeMinutes())
                .mainAddress(game.getMainAddress())
                .detailAddress(game.getDetailAddress())
                .latitude(game.getLatitude())
                .longitude(game.getLongitude())
                .status(game.getStatus())
                .isReviewDone(isReviewDone)
                .viewCount(game.getViewCount())
                .cost(game.getCost())
                .memberCount(game.getMemberCount())
                .maxMemberCount(game.getMaxMemberCount())
                .host(mapToMemberResponseDto(game.getHost()))
                .addressDepth1(game.getAddressDepth1Name())
                .addressDepth2(game.getAddressDepth2Name())
                .positions(game.getPositions())
                .members(memberResponses)
                .build();
    }
}
