package kr.pickple.back.ranking.service;

import java.util.List;

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

    public List<CrewRankingResponse> findCrewRanking() {
        return rankingJdbcRepository.getCrewRankings();
    }
}
