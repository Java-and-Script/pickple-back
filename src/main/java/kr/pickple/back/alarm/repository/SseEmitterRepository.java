package kr.pickple.back.alarm.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface SseEmitterRepository {

    SseEmitter save(final String emitterId, final SseEmitter sseEmitter);

    void saveEventCache(final String eventCacheId, final Object event);

    void deleteEventCache(final String eventCacheId);

    Map<Long, Object> findAllEventCacheStartWithByMemberId(final Long memberId);

    Map<Long,SseEmitter> findAllEmittersStartWithByMemberId(final Long memberId);

    void deleteById(final Long emitterId);

    void deleteAllEventCacheStartWithId(final Long memberId);
}
