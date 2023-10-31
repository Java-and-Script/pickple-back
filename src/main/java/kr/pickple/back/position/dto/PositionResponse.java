package kr.pickple.back.position.dto;

import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PositionResponse {

    private String name;
    private String acronym;
    private String description;

    private PositionResponse(Position position) {
        this.name = position.getName();
        this.description = position.getDescription();
        this.acronym = position.getAcronym();
    }

    public static PositionResponse from(Position position) {
        return new PositionResponse(position);
    }
}
