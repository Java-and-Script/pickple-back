package kr.pickple.back.game.repository;

import java.util.List;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.game.repository.entity.GameEntity;

public interface GameSearchRepository {

    List<GameEntity> findGamesWithInDistance(final Double latitude, final Double longitude, final Double distance);

    List<GameEntity> findGamesWithInAddress(final AddressDepth1 addressDepth1, final AddressDepth2 addressDepth2);
}
