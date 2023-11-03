package kr.pickple.back.position.dto;

import kr.pickple.back.position.domain.Position;
import lombok.Getter;

@Getter
public class PositionResponse {

    private String name;
    private String acronym;
    private String description;

    private PositionResponse(final Position position) {
        this.name = position.getName();
        this.description = position.getDescription();
        this.acronym = position.getAcronym();
    }

    public static PositionResponse from(final Position position) {
        return new PositionResponse(position);
    }
}
