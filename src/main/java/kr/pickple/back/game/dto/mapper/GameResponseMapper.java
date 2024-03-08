package kr.pickple.back.game.dto.mapper;

import java.util.List;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.dto.response.GameMemberRegistrationStatusResponse;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.dto.response.MemberGameResponse;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.mapper.MemberResponseMapper;
import kr.pickple.back.member.dto.response.MemberResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameResponseMapper {

    public static GameResponse mapToGameResponseDto(final Game gameDomain, final List<Member> members) {
        final List<MemberResponse> memberResponses = members
                .stream()
                .map(MemberResponseMapper::mapToMemberResponseDto)
                .toList();

        return GameResponse.builder()
                .id(gameDomain.getGameId())
                .content(gameDomain.getContent())
                .playDate(gameDomain.getPlayDate())
                .playStartTime(gameDomain.getPlayStartTime())
                .playEndTime(gameDomain.getPlayEndTime())
                .playTimeMinutes(gameDomain.getPlayTimeMinutes())
                .mainAddress(gameDomain.getMainAddress())
                .detailAddress(gameDomain.getDetailAddress())
                .latitude(gameDomain.getLatitude())
                .longitude(gameDomain.getLongitude())
                .status(gameDomain.getStatus())
                .viewCount(gameDomain.getViewCount())
                .cost(gameDomain.getCost())
                .memberCount(gameDomain.getMemberCount())
                .maxMemberCount(gameDomain.getMaxMemberCount())
                .host(MemberResponseMapper.mapToMemberResponseDto(gameDomain.getHost()))
                .addressDepth1(gameDomain.getAddressDepth1Name())
                .addressDepth2(gameDomain.getAddressDepth2Name())
                .positions(gameDomain.getPositions())
                .members(memberResponses)
                .build();
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
                .host(MemberResponseMapper.mapToMemberResponseDto(game.getHost()))
                .addressDepth1(game.getAddressDepth1Name())
                .addressDepth2(game.getAddressDepth2Name())
                .positions(game.getPositions())
                .members(memberResponses)
                .build();
    }

    public static GameMemberRegistrationStatusResponse mapToGameMemberRegistrationStatusResponseDto(
            final RegistrationStatus status,
            final Boolean isReviewDone
    ) {
        return GameMemberRegistrationStatusResponse
                .builder()
                .memberRegistrationStatus(status)
                .isReviewDone(isReviewDone)
                .build();
    }
}
