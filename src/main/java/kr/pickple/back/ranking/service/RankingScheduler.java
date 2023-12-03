package kr.pickple.back.ranking.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingScheduler {

    private final RankingService rankingService;
    private final RedissonClient redissonClient;

    @Scheduled(cron = "0 0 */6 * * *")
    public void refreshRankingCache() {
        RLock lock = redissonClient.getLock("refreshCrewRankingCacheLock");
        if (lock.tryLock()) {
            try {
                rankingService.putCrewRankingCache();
            } finally {
                lock.unlock();
            }
        }
    }
}
