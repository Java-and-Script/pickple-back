package kr.pickple.back.alarm.controller;

import jakarta.validation.Valid;
import kr.pickple.back.alarm.dto.request.GameAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.service.GameAlarmService;
import kr.pickple.back.auth.config.resolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game-alarms")
public class GameAlarmController {

    private final GameAlarmService gameAlarmService;

    @PatchMapping("/{gameAlarmId}")
    public ResponseEntity<Void> updateGameAlarmStatus(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameAlarmId,
            @Valid @RequestBody final GameAlarmUpdateStatusRequest gameAlarmUpdateStatusRequest
    ) {
        gameAlarmService.updateGameAlarmById(loggedInMemberId, gameAlarmId, gameAlarmUpdateStatusRequest);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }
}
