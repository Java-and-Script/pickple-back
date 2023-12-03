package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.dto.response.AlarmResponse;
import kr.pickple.back.alarm.repository.RedisEventCacheRepository;
import kr.pickple.back.alarm.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private static final Integer MAX_ALARM_COUNT = 10;
    private final SseEmitterRepository sseEmitterRepository;
    private final RedisEventCacheRepository redisEventCacheRepository;

    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
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
        sseEmitterRepository.findAllEmittersStartWithByMemberId(loggedInMemberId);
        return emitter;
    }

    public void sendCachedEventToUser(final Long memberId, final SseEmitter emitter) {
        Map<Object, Object> fallbackEmitters = redisEventCacheRepository.findAllEventCacheByMemberId(memberId);

        if (fallbackEmitters != null && !fallbackEmitters.isEmpty()) {
            fallbackEmitters.values().forEach(event -> {
                try {
                    emitter.send(SseEmitter.event().name("AlarmEvent").data(event));
                } catch (IOException e) {
                    log.error("알람 전송 중 오류가 발생했습니다.", e);
                }
            });
            redisEventCacheRepository.deleteAllEventCacheStartWithId(memberId);
        }
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

    public void limitAlarms(final Long memberId) {
        final Map<Object, Object> eventCache = redisEventCacheRepository.findAllEventCacheByMemberId(memberId);

        if (eventCache.size() > MAX_ALARM_COUNT) {
            final List<Object> sortedKeys = getSortedAlarmKeysByCreatedAt(eventCache);
            final Integer exceedAlarmCount = sortedKeys.size() - MAX_ALARM_COUNT;
            for (int i = 0; i < exceedAlarmCount; i++) {
                redisEventCacheRepository.deleteEventCache((String) sortedKeys.get(i));
            }
        }
    }

    private List<Object> getSortedAlarmKeysByCreatedAt(final Map<Object, Object> eventCache) {
        return eventCache.entrySet().stream()
                .sorted(Comparator.comparing(entry -> ((AlarmResponse) entry.getValue()).getCreatedAt()))
                .map(Map.Entry::getKey)
                .toList();
    }
}
