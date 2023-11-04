package kr.pickple.back.crew.controller;

import jakarta.validation.Valid;
import kr.pickple.back.crew.dto.request.CrewApplyRequest;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.service.CrewMemberService;
import kr.pickple.back.crew.service.CrewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crews")
public class CrewController {

    private final CrewService crewService;
    private final CrewMemberService crewMemberService;

    @PostMapping
    public ResponseEntity<CrewIdResponse> createCrew(
            @Valid @RequestBody final CrewCreateRequest crewCreateRequest
    ) {
        return ResponseEntity.status(CREATED)
                .body(crewService.createCrew(crewCreateRequest));
    }

    @GetMapping("/{crewId}")
    public ResponseEntity<CrewProfileResponse> findCrewById(
            @PathVariable final Long crewId
    ) {
        return ResponseEntity.status(OK)
                .body(crewService.findCrewById(crewId));
    }

    @PostMapping("/{crewId}/members")
    public ResponseEntity<Void> applyForCrewMemberShip(
            @PathVariable final Long crewId,
            @Valid @RequestBody final CrewApplyRequest crewApplyRequest
    ) {
        crewMemberService.applyForCrewMemberShip(crewId, crewApplyRequest);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @GetMapping("/{crewId}/members")
    public ResponseEntity<CrewProfileResponse> findAllApplyForCrewMemberShip(
            @PathVariable final Long crewId,
            @RequestParam(defaultValue = "대기") final String status
    ) {
        return ResponseEntity.status(OK)
                .body(crewMemberService.findAllApplyForCrewMemberShip(crewId, status));
    }
}
