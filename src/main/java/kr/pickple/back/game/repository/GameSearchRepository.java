package kr.pickple.back.game.repository;

import java.util.List;

import kr.pickple.back.address.repository.entity.AddressDepth1Entity;
import kr.pickple.back.address.repository.entity.AddressDepth2Entity;
import kr.pickple.back.game.repository.entity.GameEntity;

public interface GameSearchRepository {

    List<GameEntity> findGamesWithInDistance(final Double latitude, final Double longitude, final Double distance);

    List<GameEntity> findGamesWithInAddress(
            final AddressDepth1Entity addressDepth1Entity,
            final AddressDepth2Entity addressDepth2Entity
    );
}
