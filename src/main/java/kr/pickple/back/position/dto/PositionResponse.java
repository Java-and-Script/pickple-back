package kr.pickple.back.position.dto;

import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionResponse {

    private String name;
    private String acronym;
    private String description;

    public static PositionResponse from(final Position position) {
        return PositionResponse.builder()
                .name(position.getName())
                .acronym(position.getAcronym())
                .description(position.getDescription())
                .build();
    }
}
