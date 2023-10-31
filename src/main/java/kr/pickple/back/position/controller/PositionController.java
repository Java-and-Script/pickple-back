package kr.pickple.back.position.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.pickple.back.position.dto.PositionResponse;
import kr.pickple.back.position.service.PositionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public List<PositionResponse> findAllPositions() {
        return positionService.findAllPositions();
    }
}
