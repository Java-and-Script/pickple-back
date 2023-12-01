package kr.pickple.back.alarm.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;

public interface SseEmitterRepository {

    SseEmitter save(final String emitterId, final SseEmitter sseEmitter);

    Optional<SseEmitter> findById(final Long emitterId);

    Map<Long,SseEmitter> findAllEmittersStartWithByMemberId(final Long memberId);

    void deleteById(final Long emitterId);

}
