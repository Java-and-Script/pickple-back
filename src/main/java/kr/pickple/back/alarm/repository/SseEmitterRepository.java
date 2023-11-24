package kr.pickple.back.alarm.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

public interface SseEmitterRepository {

    SseEmitter save(final String emitterId,final SseEmitter sseEmitter);

    void saveEventCache(final String eventCacheId,final Object event);

    Map<Long, SseEmitter> findAllEmitterStartWithByMemberId(final Long memberId);

    Map<Long, SseEmitter> findAllEmitterStartWithByMemberIdInList(final List<Long> memberId);

    Map<Long, Object> findAllEventCacheStartWithByMemberId(final Long memberId);

    void deleteById(final Long emitterId);

    void deleteAllEmitterStartWithId(final Long memberId);

    void deleteAllEventCacheStartWithId(final Long memberId);

    void notify(final Long memberId, final Object event);
}
