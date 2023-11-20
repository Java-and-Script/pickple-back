package kr.pickple.back.alaram.controller;

import kr.pickple.back.alaram.dto.response.AlaramUnReadCountResponse;
import kr.pickple.back.alaram.service.AlaramService;
import kr.pickple.back.auth.config.resolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {

    private final AlaramService alarmService;

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
    public ResponseEntity<AlaramUnReadCountResponse> findUnreadAlarmResponse(
            @PathVariable final Login loggedInMemberId
    ) {
        //알람 서비스 중, 읽지 않은 알람 계산

        return ResponseEntity.status(OK)
                .body(AlaramService.countUnreadAlaram());
    }

//    @GetMapping
//    public ResponseEntity<List<>> findAllAlarams(
//            @Login final  Long loggedInMemberId,
//            final Cursor cursor
//            ){
//        //커시 기반 조회 서비스 구현 후, 채움
//        return  ResponseEntity.status(OK)
//                .body(AlaramService.findAllAlarms());
//    }

    @PatchMapping("/{alaramId}")
    public ResponseEntity<Void> updateAlaramStatus(
            @Login final Long loggedInMemberId,
            @PathVariable final Long alaramId
    ) {
        //알람 서비스 중, 수정 기반
        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAlarams(
            @Login final Long loggedInMemberId,
            final Cursor cursor
    ) {
        //알람 삭제 서비스, 커서 기반
        return ResponseEntity.status(NO_CONTENT)
                .build();
    }
}
