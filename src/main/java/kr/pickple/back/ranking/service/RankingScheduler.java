package kr.pickple.back.ranking.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingScheduler {

    private final RankingService rankingService;

    @Scheduled(cron = "0 0 */6 * * *")
    public void refreshRankingCache() {
        rankingService.putCrewRankingCache();
    }
}
