package kr.pickple.back.alarm.repository;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log4j2
@Repository
@NoArgsConstructor
public class SseEmitterRepositoryImpl implements SseEmitterRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(final String emitterId, final SseEmitter sseEmitter) {
        emitters.put(Long.parseLong(emitterId), sseEmitter);

        log.debug("new emitter added: {}", sseEmitter);
        log.debug("emitter list size: {}", emitters.size());
        return sseEmitter;
    }

    @Override
    public void saveEventCache(final String eventCacheId, final Object event) {
        eventCache.put(Long.parseLong(eventCacheId), event);

        log.debug("new event cached: {}", event);
        log.debug("event cache size: {}", eventCache.size());
    }

    @Override
    public Map<Long, SseEmitter> findAllEmitterStartWithByMemberId(final Long memberId) {
        final Map<Long, SseEmitter> result = emitters.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(memberId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        log.debug("emitters for memberId {}: {}", memberId, result);
        return result;
    }

    @Override
    public Map<Long, SseEmitter> findAllEmitterStartWithByMemberIdInList(final List<Long> memberId) {
        final Map<Long, SseEmitter> result = emitters.entrySet().stream()
                .filter(entry -> memberId.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        log.debug("emitters for memberIds {}: {}", memberId, result);
        return result;
    }

    @Override
    public Map<Long, Object> findAllEventCacheStartWithByMemberId(final Long memberId) {
        final Map<Long, Object> result = eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith(memberId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        log.debug("event cache for memberId {}: {}", memberId, result);
        return result;
    }

    @Override
    public void deleteById(final Long emitterId) {
        emitters.remove(emitterId);

        log.debug("emitter with id {} removed", emitterId);
    }

    @Override
    public void deleteAllEmitterStartWithId(final Long memberId) {
        emitters.entrySet().removeIf(entry -> entry.getKey().toString().startsWith(memberId.toString()));

        log.debug("all emitters starting with memberId {} removed", memberId);
    }

    @Override
    public void deleteAllEventCacheStartWithId(final Long memberId) {
        eventCache.entrySet().removeIf(entry -> entry.getKey().toString().startsWith(memberId.toString()));

        log.debug("all event cache starting with memberId {} removed", memberId);
    }

    @Override
    public void notify(final Long loggedInMemberId, final Object event) {
        final Map<Long, SseEmitter> emitters = findAllEmitterStartWithByMemberId(loggedInMemberId);

        emitters.values().forEach(emitter -> {
            try {
                emitter.send(event);

                log.debug("event {} sent to emitter {}", event, emitter);
            } catch (IOException e) {
                deleteById(loggedInMemberId);
                emitter.completeWithError(e);

                log.debug("error sending event to emitter", e);
            }
        });
    }
}
