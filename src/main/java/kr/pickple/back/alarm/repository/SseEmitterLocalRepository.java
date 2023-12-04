package kr.pickple.back.alarm.repository;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Repository
@NoArgsConstructor
public class SseEmitterLocalRepository implements SseEmitterRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(final String emitterId, final SseEmitter sseEmitter) {
        emitters.put(Long.parseLong(emitterId), sseEmitter);
        return sseEmitter;
    }

    @Override
    public Optional<SseEmitter> findById(final Long emitterId) {
        return Optional.ofNullable(emitters.get(emitterId));
    }

    @Override
    public void deleteById(final Long emitterId) {
        emitters.remove(emitterId);
    }
}
