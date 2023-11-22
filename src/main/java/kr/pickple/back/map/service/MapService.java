package kr.pickple.back.map.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.map.domain.MapPolygon;
import kr.pickple.back.map.repository.MapPolygonRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapPolygonRepository mapPolygonRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheManager = "caffeineCacheManager", cacheNames = "polygon", key = "#mainAddressResponse.addressDepth2.name")
    public MapPolygon findMapPolygonByMainAddress(final MainAddressResponse mainAddressResponse) {
        return mapPolygonRepository.findByAddressDepth1AndAddressDepth2(
                mainAddressResponse.getAddressDepth1(),
                mainAddressResponse.getAddressDepth2()
        );
    }
}
