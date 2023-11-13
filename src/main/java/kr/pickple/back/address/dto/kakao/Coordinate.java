package kr.pickple.back.address.dto.kakao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coordinate {

    private Double x;
    private Double y;
}
