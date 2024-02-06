package kr.pickple.back.map.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressId;
import kr.pickple.back.map.domain.MapPolygon;
import kr.pickple.back.map.repository.MapPolygonRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapPolygonRepository mapPolygonRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "caffeineCacheManager", cacheNames = "polygon", key = "#mainAddressId.addressDepth2.name")
    public MapPolygon findMapPolygonByMainAddress(final MainAddressId mainAddressId) {

        return mapPolygonRepository.findByAddressDepth1IdAndAddressDepth2Id(
                mainAddressId.getAddressDepth1Id(),
                mainAddressId.getAddressDepth2Id()
        );
    }
}
