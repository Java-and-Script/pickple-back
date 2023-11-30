package kr.pickple.back.ranking.service;

import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.ranking.dto.CrewRankingResponse;
import kr.pickple.back.ranking.repository.RankingJdbcRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final RankingJdbcRepository rankingJdbcRepository;

    @Cacheable(cacheManager = "caffeineCacheManager", cacheNames = "ranking", key = "'crew'")
    public List<CrewRankingResponse> findCrewRanking() {
        return putCrewRankingCache();
    }

    @CachePut(cacheManager = "caffeineCacheManager", cacheNames = "ranking", key = "'crew'")
    public List<CrewRankingResponse> putCrewRankingCache() {
        final List<CrewRankingResponse> crewRankings = rankingJdbcRepository.getCrewRankings();

        return crewRankings;
    }
}
