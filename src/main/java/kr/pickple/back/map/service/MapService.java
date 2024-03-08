package kr.pickple.back.map.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.map.domain.MapPolygon;
import kr.pickple.back.map.repository.MapPolygonRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapPolygonRepository mapPolygonRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "caffeineCacheManager", cacheNames = "polygon", key = "#mainAddress.addressDepth2.name")
    public MapPolygon findMapPolygonByMainAddress(final MainAddress mainAddress) {

        return mapPolygonRepository.findByAddressDepth1IdAndAddressDepth2Id(
                mainAddress.getAddressDepth1Id(),
                mainAddress.getAddressDepth2Id()
        );
    }
}
