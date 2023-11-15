package kr.pickple.back.game.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.game.domain.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

    String HAVERSINE_FORMULA = "(6371 * acos(cos(radians(:latitude)) * cos(radians(g.latitude)) *" +
            " cos(radians(g.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(g.latitude))))";

    Page<Game> findByAddressDepth1AndAddressDepth2(final AddressDepth1 addressDepth1, final AddressDepth2 addressDepth2,
            final Pageable pageable);

    @Query("SELECT g FROM Game g WHERE " + HAVERSINE_FORMULA + " < :distance ORDER BY " + HAVERSINE_FORMULA)
    List<Game> findGamesWithInDistance(
            final Double latitude,
            final Double longitude,
            final Double distance
    );
}
