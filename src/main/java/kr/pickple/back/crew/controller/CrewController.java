package kr.pickple.back.crew.controller;

import jakarta.validation.Valid;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.service.CrewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crews")
public class CrewController {

    private final CrewService crewService;

    @PostMapping
    public ResponseEntity<CrewIdResponse> createCrew(
            @Valid @RequestBody final CrewCreateRequest crewCreateRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crewService.createCrew(crewCreateRequest));
    }
}
