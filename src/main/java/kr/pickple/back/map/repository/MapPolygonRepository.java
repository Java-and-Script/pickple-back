package kr.pickple.back.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.map.domain.MapPolygon;

public interface MapPolygonRepository extends JpaRepository<MapPolygon, Long> {

    MapPolygon findByAddressDepth1AndAddressDepth2(final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2);
}
