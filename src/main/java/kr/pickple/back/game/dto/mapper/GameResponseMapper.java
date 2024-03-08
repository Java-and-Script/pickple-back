package kr.pickple.back.game.dto.mapper;

import java.util.List;

import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.mapper.MemberResponseMapper;
import kr.pickple.back.member.dto.response.MemberResponse;

public class GameResponseMapper {

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
}
