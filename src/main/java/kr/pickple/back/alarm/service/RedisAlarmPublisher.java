package kr.pickple.back.alarm.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisAlarmPublisher implements AlarmPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(final Long memberId, final String alarm) {
        final String fullMessage = memberId + ":" + alarm;
        redisTemplate.convertAndSend("pubsub:queue", fullMessage);
    }
}
