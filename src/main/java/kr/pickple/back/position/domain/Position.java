package kr.pickple.back.position.domain;

import static kr.pickple.back.position.exception.PositionExceptionCode.*;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import kr.pickple.back.position.exception.PositionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {

    CENTER("센터", "C", "팀의 공격과 수비에서 중추적인 역할"),
    POWER_FORWARD("파워 포워드", "PF", "득점, 리바운드, 수비에서의 강인한 플레이를 담당하는 역할"),
    SMALL_FORWARD("스몰 포워드", "SF", "공격과 수비 양면에서 다재다능한 역할"),
    POINT_GUARD("포인트 가드", "PG", "공격 조직과 전술적인 플레이를 주도하는 주장 역할"),
    SHOOTING_GUARD("슈팅 가드", "SG", "주로 3점슛을 통해 팀의 주요 득점 옵션을 담당하는 역할"),
    EMPTY("포지션 없음", "없음", "포지션을 별도로 선택하지 않음"),
    ;

    private static final Map<String, Position> positionMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(Position::getAcronym, Function.identity())));

    private final String name;

    @JsonValue
    private final String acronym;

    private final String description;

    @JsonCreator
    public static Position fromGamePositions(final String positionAcronym) {
        if (positionMap.containsKey(positionAcronym)) {
            return positionMap.get(positionAcronym);
        }

        throw new PositionException(POSITION_NOT_FOUND, positionAcronym);
    }
}
