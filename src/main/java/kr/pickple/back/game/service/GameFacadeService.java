package kr.pickple.back.game.service;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.dto.response.GamesAndLocationResponse;
import kr.pickple.back.map.domain.MapPolygon;
import kr.pickple.back.map.service.MapService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameFacadeService {

    private final GameService gameService;
    private final MapService mapService;
    private final AddressService addressService;

    public GamesAndLocationResponse findGamesWithInAddress(
            final String addressDepth1,
            final String addressDepth2
    ) {
        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByNames(addressDepth1,
                addressDepth2);

        final List<GameResponse> gameResponses = gameService.findGamesWithInAddress(mainAddressResponse);
        final MapPolygon mapPolygon = mapService.findMapPolygonByMainAddress(mainAddressResponse);

        return GamesAndLocationResponse.of(gameResponses, mapPolygon);
    }
}
