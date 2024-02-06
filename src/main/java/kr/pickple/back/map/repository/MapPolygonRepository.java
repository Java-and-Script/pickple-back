package kr.pickple.back.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.map.domain.MapPolygon;

public interface MapPolygonRepository extends JpaRepository<MapPolygon, Long> {

    MapPolygon findByAddressDepth1IdAndAddressDepth2Id(
            final Long addressDepth1Id,
            final Long addressDepth2Id
    );
}
