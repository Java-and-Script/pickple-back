package kr.pickple.back.alarm.controller;

import kr.pickple.back.alarm.dto.response.AlarmExistStatusResponse;
import kr.pickple.back.alarm.dto.response.AlarmResponse;
import kr.pickple.back.alarm.service.AlarmService;
import kr.pickple.back.alarm.service.SseEmitterService;
import kr.pickple.back.alarm.util.CursorResult;
import kr.pickple.back.auth.config.resolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {

    private final AlarmService alarmService;
    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribeToSse(
            @Login final Long loggedInMemberId
    ) {
        final SseEmitter emitter = alarmService.subscribeToSse(loggedInMemberId);
        sseEmitterService.sendCachedEventToUser(loggedInMemberId, emitter);

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

    @GetMapping
    public ResponseEntity<CursorResult<AlarmResponse>> findAllAlarms(
            @Login final Long loggedInMemberId,
            @RequestParam(value = "lastCrewAlarmId", required = false) Long lastCrewAlarmId,
            @RequestParam(value = "lastGameAlarmId", required = false) Long lastGameAlarmId,
            @RequestParam(value = "size", defaultValue = "6") int size
    ) {
        final CursorResult<AlarmResponse> result = alarmService.findAllAlarms(
                loggedInMemberId, lastCrewAlarmId, lastGameAlarmId, size);
        return ResponseEntity.ok(result);
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
