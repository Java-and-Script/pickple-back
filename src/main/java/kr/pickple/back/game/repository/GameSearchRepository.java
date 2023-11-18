package kr.pickple.back.game.repository;

import java.util.List;

import kr.pickple.back.game.domain.Game;

public interface GameSearchRepository {

    List<Game> findGamesWithInDistance(final Double latitude, final Double longitude, final Double distance);
}
