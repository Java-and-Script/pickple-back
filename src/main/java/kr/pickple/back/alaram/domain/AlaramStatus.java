package kr.pickple.back.alaram.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import kr.pickple.back.crew.exception.CrewException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kr.pickple.back.alaram.exception.AlaramExceptionCode.ALARM_STATUS_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum AlaramStatus {

    TRUE("읽음", true),
    FALSE("읽지 않음", false);

    private static final Map<String, AlaramStatus> alaramStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(AlaramStatus::getDescription, Function.identity())));

    private final String description;
    private final Boolean booleanValue;

    @JsonCreator
    public static AlaramStatus from(final String description) {
        if (alaramStatusMap.containsKey(description)) {
            return alaramStatusMap.get(description);
        }
        throw new CrewException(ALARM_STATUS_NOT_FOUND, description);
    }

    @JsonValue
    public Boolean getBooleanValue() {
        return booleanValue;
    }
}
