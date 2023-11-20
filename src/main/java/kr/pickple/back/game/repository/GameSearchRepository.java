package kr.pickple.back.game.repository;

import java.util.List;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.game.domain.Game;

public interface GameSearchRepository {

    List<Game> findGamesWithInDistance(final Double latitude, final Double longitude, final Double distance);

    List<Game> findGamesWithInAddress(final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2);
}
