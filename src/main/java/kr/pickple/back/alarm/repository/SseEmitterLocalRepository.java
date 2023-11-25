package kr.pickple.back.alarm.repository;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log4j2
@Repository
@NoArgsConstructor
public class SseEmitterLocalRepository implements SseEmitterRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Object> fallbackEmitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(final String emitterId, final SseEmitter sseEmitter) {
        emitters.put(Long.parseLong(emitterId), sseEmitter);
        return sseEmitter;
    }

    @Override
    public SseEmitter findEmitterById(final Long emitterId) {
        return emitters.get(emitterId);
    }

    @Override
    public void saveEventCache(final String eventCacheId, final Object event) {
        fallbackEmitters.put(Long.parseLong(eventCacheId), event);
    }

    @Override
    public void deleteEventCache(final String eventCacheId) {
        fallbackEmitters.remove(Long.parseLong(eventCacheId));
    }

    @Override
    public Map<Long, Object> findAllEventCacheStartWithByMemberId(final Long memberId) {
        final Map<Long, Object> result = fallbackEmitters.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(memberId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return result;
    }

    @Override
    public void deleteById(final Long emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public void deleteAllEventCacheStartWithId(final Long memberId) {
        fallbackEmitters.entrySet().removeIf(entry -> entry.getKey().toString().startsWith(memberId.toString()));
    }
}
