package kr.pickple.back.game.service;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.dto.response.GamesAndLocationResponse;
import kr.pickple.back.map.domain.MapPolygon;
import kr.pickple.back.map.service.MapService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameFacadeService {

    private final AddressReader addressReader;

    private final GameService gameService;
    private final MapService mapService;

    public GamesAndLocationResponse findGamesWithInAddress(final String addressDepth1, final String addressDepth2) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(addressDepth1, addressDepth2);
        final List<GameResponse> gameResponses = gameService.findGamesWithInAddress(mainAddress);
        final MapPolygon mapPolygon = mapService.findMapPolygonByMainAddress(mainAddress);

        return GamesAndLocationResponse.of(gameResponses, mapPolygon);
    }
}
