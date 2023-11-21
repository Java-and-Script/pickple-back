package kr.pickple.back.alarm.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEmitters {

    //private final SseEmitters sseEmitters;

    private final ConcurrentMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(final Long id, final SseEmitter emitter) {
        this.emitters.put(id, emitter);

        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(id);
        });

        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    public SseEmitter get(final Long id) {
        return this.emitters.get(id);
    }

    public void remove(final Long id) {
        this.emitters.remove(id);
    }

    public void notify(final Long loggedInMemberId, final Object event) {
        final SseEmitter emitter = this.get(loggedInMemberId);
        if (emitter != null) {
            try {
                emitter.send(event);
            } catch (IOException e) {
                this.remove(loggedInMemberId);
                emitter.completeWithError(e);
            }
        }
    }
}
