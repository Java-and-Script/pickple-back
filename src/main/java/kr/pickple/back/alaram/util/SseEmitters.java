package kr.pickple.back.alaram.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseEmitters {

    private final ConcurrentMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(final Long id, final SseEmitter emitter) {
        this.emitters.put(id, emitter);

        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(id);    // 만료되면 리스트에서 삭제
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
}
