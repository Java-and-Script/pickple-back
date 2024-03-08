package kr.pickple.back.game.dto.mapper;

import java.time.LocalTime;

import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.game.domain.NewGame;
import kr.pickple.back.game.dto.request.GameCreateRequest;

public class GameRequestMapper {

    public static NewGame mapToNewGameDomain(final GameCreateRequest gameCreateRequest, final MainAddress mainAddress) {
        final LocalTime playEndTime = gameCreateRequest.getPlayStartTime()
                .plusMinutes(gameCreateRequest.getPlayTimeMinutes());

        return NewGame.builder()
                .content(gameCreateRequest.getContent())
                .playDate(gameCreateRequest.getPlayDate())
                .playStartTime(gameCreateRequest.getPlayStartTime())
                .playEndTime(playEndTime)
                .playTimeMinutes(gameCreateRequest.getPlayTimeMinutes())
                .mainAddress(gameCreateRequest.getMainAddress())
                .detailAddress(gameCreateRequest.getDetailAddress())
                .cost(gameCreateRequest.getCost())
                .maxMemberCount(gameCreateRequest.getMaxMemberCount())
                .positions(gameCreateRequest.getPositions())
                .addressDepth1Name(mainAddress.getAddressDepth1Name())
                .addressDepth2Name(mainAddress.getAddressDepth2Name())
                .build();
    }
}
