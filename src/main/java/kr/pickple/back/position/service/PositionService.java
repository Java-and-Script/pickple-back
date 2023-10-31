package kr.pickple.back.position.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.pickple.back.position.domain.Position;
import kr.pickple.back.position.dto.PositionResponse;

@Service
public class PositionService {
    public List<PositionResponse> findAllPositions() {
        List<Position> positions = Arrays.stream(Position.values()).toList();

        return positions.stream()
                .map(PositionResponse::from)
                .toList();
    }
}
