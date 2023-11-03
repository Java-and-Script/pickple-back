package kr.pickple.back.crew.controller;

import jakarta.validation.Valid;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.service.CrewService;
import lombok.RequiredArgsConstructor;
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
}
