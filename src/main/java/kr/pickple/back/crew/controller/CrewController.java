package kr.pickple.back.crew.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.service.CrewService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crews")
public class CrewController {

    private final CrewService crewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CrewIdResponse createCrew(@Valid @RequestBody CrewCreateRequest crewCreateRequest) {
        return crewService.createCrew(crewCreateRequest);
    }
}
