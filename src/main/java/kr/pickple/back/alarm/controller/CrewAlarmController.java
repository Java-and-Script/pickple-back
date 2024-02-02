package kr.pickple.back.alarm.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.pickple.back.alarm.dto.request.CrewAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.service.CrewAlarmService;
import kr.pickple.back.auth.config.resolver.Login;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crew-alarms")
public class CrewAlarmController {

    private final CrewAlarmService crewAlarmService;

    @PatchMapping("/{crewAlarmId}")
    public ResponseEntity<Void> updateCrewAlarmStatus(
            @Login final Long loggedInMemberId,
            @PathVariable final Long crewAlarmId,
            @Valid @RequestBody final CrewAlarmUpdateStatusRequest crewAlarmUpdateStatusRequest
    ) {
        crewAlarmService.updateCrewAlarmById(loggedInMemberId, crewAlarmId, crewAlarmUpdateStatusRequest);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }
}
