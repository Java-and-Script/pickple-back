package kr.pickple.back.crew.controller;

import jakarta.validation.Valid;
import kr.pickple.back.crew.dto.request.CrewApplyRequest;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewMemberIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.service.CrewMemberService;
import kr.pickple.back.crew.service.CrewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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
    @ResponseStatus(HttpStatus.NO_CONTENT)

    public CrewMemberIdResponse applyForCrewMemberShip(
            @PathVariable("crewId") Long crewId,
            @Valid @RequestBody CrewApplyRequest crewApplyRequest
    ) {
        return crewMemberService.applyForCrewMemberShip(crewId, crewApplyRequest);
    }

    @PostMapping("/{crewId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)

    public CrewMemberIdResponse applyForCrewMemberShip(
            @PathVariable("crewId") Long crewId,
            @Valid @RequestBody CrewApplyRequest crewApplyRequest
    ) {
        return crewMemberService.applyForCrewMemberShip(crewId, crewApplyRequest);
    }
}
