package kr.pickple.back.game.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.dto.mapper.GameResponseMapper;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.dto.response.GamesAndLocationResponse;
import kr.pickple.back.game.implement.GameMemberReader;
import kr.pickple.back.game.implement.GameReader;
import kr.pickple.back.map.domain.MapPolygon;
import kr.pickple.back.map.service.MapService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameFacadeService {

    private final AddressReader addressReader;
    private final MapService mapService;
    private final GameReader gameReader;
    private final GameMemberReader gameMemberReader;

    /**
     * 특정 지역의 게스트 모집글 조회
     */
    public GamesAndLocationResponse findGamesWithInAddress(final String addressDepth1, final String addressDepth2) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(addressDepth1, addressDepth2);

        final List<Game> games = gameReader.readAllWithInAddress(mainAddress);

        final List<GameResponse> gameResponses = games.stream()
                .map(gameDomain -> GameResponseMapper.mapToGameResponseDto(
                                gameDomain,
                                gameMemberReader.readMembersByGameIdAndStatus(gameDomain.getGameId(), CONFIRMED)
                        )
                )
                .toList();

        final MapPolygon mapPolygon = mapService.findMapPolygonByMainAddress(mainAddress);

        return GamesAndLocationResponse.of(gameResponses, mapPolygon);
    }
}
