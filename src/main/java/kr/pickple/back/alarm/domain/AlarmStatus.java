package kr.pickple.back.alarm.domain;

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

import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_STATUS_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum AlarmStatus {

    TRUE("읽음", true),
    FALSE("읽지 않음", false);

    private static final Map<Boolean, AlarmStatus> alarmStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(AlarmStatus::getBooleanValue, Function.identity())));

    private final String description;
    private final Boolean booleanValue;

    @JsonCreator
    public static AlarmStatus from(final Boolean booleanValue) {
        if (alarmStatusMap.containsKey(booleanValue)) {
            return alarmStatusMap.get(booleanValue);
        }
        throw new CrewException(ALARM_STATUS_NOT_FOUND, booleanValue);
    }

    @JsonValue
    public Boolean getBooleanValue() {
        return booleanValue;
    }
}
