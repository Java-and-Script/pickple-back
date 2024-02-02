package kr.pickple.back.alarm.repository;

import java.util.Optional;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterRepository {

    SseEmitter save(final String emitterId, final SseEmitter sseEmitter);

    Optional<SseEmitter> findById(final Long emitterId);

    void deleteById(final Long emitterId);
}
