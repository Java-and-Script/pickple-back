package kr.pickple.back.auth.repository;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;

    public RedisRepository(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public <T> void saveHash(final String key, final String field, final T value, final Long duration) {
        hashOperations.put(key, field, value);
        redisTemplate.expire(key, duration, TimeUnit.SECONDS);
    }

    public <T> T findHash(final String key, final String field) {
        return (T)hashOperations.get(key, field);
    }

    public Boolean existsHash(final String key, final String field) {
        return hashOperations.hasKey(key, field);
    }

    public void deleteHash(final String key, final String field) {
        hashOperations.delete(key, field);
    }
}
