package kr.pickple.back.position.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.pickple.back.position.domain.Position;
import kr.pickple.back.position.dto.PositionResponse;

@SpringBootTest
class PositionServiceTest {

    @Autowired
    private PositionService positionService;

    @Test
    @DisplayName("포지션 정보 조회 시, 전체 포지션 정보를 반환한다.")
    void findAllPositions_Success() {
        //when
        List<PositionResponse> Positions = positionService.findAllPositions();

        //then
        assertThat(Positions.get(0).getName()).isEqualTo(Position.CENTER.getName());
        assertThat(Positions.get(0).getAcronym()).isEqualTo(Position.CENTER.getAcronym());
        assertThat(Positions.get(0).getDescription()).isEqualTo(Position.CENTER.getDescription());

        assertThat(Positions.get(1).getName()).isEqualTo(Position.POWER_FORWARD.getName());
        assertThat(Positions.get(1).getAcronym()).isEqualTo(Position.POWER_FORWARD.getAcronym());
        assertThat(Positions.get(1).getDescription()).isEqualTo(Position.POWER_FORWARD.getDescription());

        assertThat(Positions.get(2).getName()).isEqualTo(Position.SMALL_FORWARD.getName());
        assertThat(Positions.get(2).getAcronym()).isEqualTo(Position.SMALL_FORWARD.getAcronym());
        assertThat(Positions.get(2).getDescription()).isEqualTo(Position.SMALL_FORWARD.getDescription());

        assertThat(Positions.get(3).getName()).isEqualTo(Position.POINT_GUARD.getName());
        assertThat(Positions.get(3).getAcronym()).isEqualTo(Position.POINT_GUARD.getAcronym());
        assertThat(Positions.get(3).getDescription()).isEqualTo(Position.POINT_GUARD.getDescription());

        assertThat(Positions.get(4).getName()).isEqualTo(Position.SHOOTING_GUARD.getName());
        assertThat(Positions.get(4).getAcronym()).isEqualTo(Position.SHOOTING_GUARD.getAcronym());
        assertThat(Positions.get(4).getDescription()).isEqualTo(Position.SHOOTING_GUARD.getDescription());

        assertThat(Positions.get(5).getName()).isEqualTo(Position.EMPTY.getName());
        assertThat(Positions.get(5).getAcronym()).isEqualTo(Position.EMPTY.getAcronym());
        assertThat(Positions.get(5).getDescription()).isEqualTo(Position.EMPTY.getDescription());
    }
}