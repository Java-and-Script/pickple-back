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

import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_EXISTS_STATUS_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum AlarmExistsStatus {

    EXISTS("읽지 않은 알람이 있음", true),
    NOT_EXISTS("읽지 않은 알람이 없음", false);

    private static final Map<String, AlarmExistsStatus> alarmExistsStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(AlarmExistsStatus::getDescription, Function.identity())));

    private final String description;
    private final Boolean booleanValue;

    @JsonCreator
    public static AlarmExistsStatus from(final String description) {
        if (alarmExistsStatusMap.containsKey(description)) {
            return alarmExistsStatusMap.get(description);
        }
        throw new CrewException(ALARM_EXISTS_STATUS_NOT_FOUND, description);
    }

    @JsonValue
    public Boolean getBooleanValue() {
        return booleanValue;
    }
}
