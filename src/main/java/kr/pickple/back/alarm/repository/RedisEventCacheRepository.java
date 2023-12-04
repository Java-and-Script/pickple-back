package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.dto.response.AlarmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisEventCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveEventCache(final String eventCacheId, final Object event) {
        redisTemplate.opsForHash().put("EventCache", eventCacheId, event);
    }

    public Map<Object, Object> findAllEventCacheByMemberId(final Long memberId) {
        return redisTemplate.opsForHash().entries("EventCache:" + memberId);
    }

    public List<Object> findLatestEventCacheByMemberId(final Long memberId, final int count) {
        final Map<Object, Object> eventCache = findAllEventCacheByMemberId(memberId);
        return eventCache.values().stream()
                .sorted(Comparator.comparing(event -> ((AlarmResponse) event).getCreatedAt()).reversed())
                .limit(count)
                .toList();
    }

    public void deleteEventCache(final String eventCacheId) {
        redisTemplate.opsForHash().delete("EventCache", eventCacheId);
    }

    public void deleteAllEventCacheStartWithId(final Long memberId) {
        final Set<String> keys = redisTemplate.keys("EventCache:" + String.valueOf(memberId) + "*");

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
