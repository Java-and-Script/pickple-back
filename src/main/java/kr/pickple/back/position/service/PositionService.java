package kr.pickple.back.position.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.pickple.back.position.domain.Position;
import kr.pickple.back.position.dto.PositionResponse;

@Service
public class PositionService {

    public List<PositionResponse> findAllPositions() {
        return Arrays.stream(Position.values())
                .map(PositionResponse::from)
                .toList();
    }
}
