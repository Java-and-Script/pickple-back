package kr.pickple.back.alarm.controller;

import jakarta.validation.Valid;
import kr.pickple.back.alarm.dto.request.CrewAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.service.CrewAlarmService;
import kr.pickple.back.auth.config.resolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;

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
