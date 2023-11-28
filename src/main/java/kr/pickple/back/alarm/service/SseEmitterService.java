package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onTimeout(() -> {
            sseEmitterRepository.deleteById(loggedInMemberId);
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("AlarmSseConnect")
                    .data("사용자에 대한 알람 SSE 연결이 정상적으로 처리되었습니다."));
        } catch (IOException e) {
            sseEmitterRepository.deleteById(loggedInMemberId);
            emitter.completeWithError(e);
        }
        sseEmitterRepository.save(String.valueOf(loggedInMemberId), emitter);
        sseEmitterRepository.findAllEmittersStartWithByMemberId(loggedInMemberId);
        return emitter;
    }

    public void sendCachedEventToUser(final Long memberId, final SseEmitter emitter) {
        Map<Long, Object> fallbackEmitters = sseEmitterRepository.findAllEventCacheStartWithByMemberId(memberId);

        if (fallbackEmitters != null && !fallbackEmitters.isEmpty()) {
            fallbackEmitters.values().forEach(event -> {
                try {
                    emitter.send(SseEmitter.event().name("AlarmEvent").data(event));
                } catch (IOException e) {
                    log.error("알람 전송 중 오류가 발생했습니다.", e);
                }
                sseEmitterRepository.deleteAllEventCacheStartWithId(memberId);
            });
        }
    }

    public <T> void sendAlarm(final Long memberId, final T responseDto) {
        final SseEmitter emitter = subscribeToSse(memberId);

        try {
            emitter.send(SseEmitter.event().name("AlarmEvent").data(responseDto));
            sseEmitterRepository.deleteEventCache(String.valueOf(memberId));
        } catch (Exception e) {
            sseEmitterRepository.saveEventCache(String.valueOf(memberId), responseDto);
            sseEmitterRepository.deleteById(memberId);
            log.error("알람 전송 중 오류가 발생했습니다. memberId: {}", memberId, e);
        }
    }
}
