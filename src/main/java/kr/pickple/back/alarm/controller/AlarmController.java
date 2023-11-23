package kr.pickple.back.alarm.controller;

import kr.pickple.back.alarm.dto.response.AlarmExistStatusResponse;
import kr.pickple.back.alarm.service.AlarmService;
import kr.pickple.back.auth.config.resolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribeToSse(
            @Login final Long loggedInMemberId
    ) {
        final SseEmitter emitter = alarmService.subscribeToSse(loggedInMemberId);

        return ResponseEntity.status(OK)
                .header("X-Accel-Buffering", "no")
                .body(emitter);
    }

    @GetMapping("/unread")
    public ResponseEntity<AlarmExistStatusResponse> findUnreadAlarm(
            @Login final Long loggedInMemberId
    ) {
        AlarmExistStatusResponse response = alarmService.checkUnReadAlarms(loggedInMemberId);

        return ResponseEntity
                .status(OK)
                .body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAlarms(
            @Login final Long loggedInMemberId
    ) {
        alarmService.deleteAllAlarms(loggedInMemberId);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }
}
