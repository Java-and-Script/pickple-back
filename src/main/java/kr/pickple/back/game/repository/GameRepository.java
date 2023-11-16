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

    Page<Game> findByAddressDepth1AndAddressDepth2(final AddressDepth1 addressDepth1, final AddressDepth2 addressDepth2,
            final Pageable pageable);

    @Query("SELECT g "
            + "FROM Game g  "
            + "WHERE ST_Contains(ST_Buffer(ST_GeomFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326), :distance), g.point)"
            + "ORDER BY ST_Distance_Sphere(g.point, ST_GeomFromText(CONCAT('POINT(', :latitude, ' ', :longitude, ')'), 4326))"
    )
    List<Game> findGamesWithInDistance(
            final Double latitude,
            final Double longitude,
            final Double distance
    );
}
