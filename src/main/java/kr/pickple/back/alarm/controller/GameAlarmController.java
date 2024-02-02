package kr.pickple.back.alarm.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.pickple.back.alarm.dto.request.GameAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.service.GameAlarmService;
import kr.pickple.back.auth.config.resolver.Login;
import lombok.RequiredArgsConstructor;

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
