package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.repository.RedisEventCacheRepository;
import kr.pickple.back.alarm.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60 * 7;
    private final SseEmitterRepository sseEmitterRepository;
    private final RedisEventCacheRepository redisEventCacheRepository;

    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
        final Optional<SseEmitter> oldEmitterOptional = sseEmitterRepository.findById(loggedInMemberId);
        oldEmitterOptional.ifPresent(emitter -> {
            emitter.complete();
            sseEmitterRepository.deleteById(loggedInMemberId);
        });

        final SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

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
        sendCachedEventToUser(loggedInMemberId, emitter);

        return emitter;
    }

    public void sendCachedEventToUser(final Long memberId, final SseEmitter emitter) {
        final List<Object> cachedEvents = redisEventCacheRepository.findLatestEventCacheByMemberId(memberId, 10);

        for (Object event : cachedEvents) {
            try {
                emitter.send(SseEmitter.event().name("AlarmEvent").data(event));
            } catch (IOException e) {
                log.error("알람 전송 중 오류가 발생했습니다.", e);
            }
        }
        redisEventCacheRepository.deleteAllEventCacheStartWithId(memberId);
    }

    public <T> void sendAlarm(final Long memberId, final T responseDto) {
        final Optional<SseEmitter> optionalEmitter = sseEmitterRepository.findById(memberId);

        optionalEmitter.ifPresentOrElse(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("AlarmEvent").data(responseDto));
                redisEventCacheRepository.deleteEventCache(String.valueOf(memberId));
            } catch (Exception e) {
                redisEventCacheRepository.saveEventCache(String.valueOf(memberId), responseDto);
                log.error("알람 전송 중 오류가 발생했습니다. memberId: {}", memberId, e);
            }
        }, () -> {
            log.error("해당 memberId에 대한 SseEmitter를 찾을 수 없습니다. memberId: {}", memberId);
        });
    }
}
