package kr.pickple.back.alarm.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import kr.pickple.back.alarm.exception.AlarmException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_TYPE_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum CrewAlarmType {

    CREW_LEADER_WAITING("가입 수락을 기다리고 있어요"),
    CREW_ACCEPT("크루 가입이 수락되었어요"),
    CREW_DENIED("크루 가입이 거절되었어요"),
    ;

    private static final Map<String, CrewAlarmType> crewAlarmTypeMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(CrewAlarmType::name, Function.identity())));

    private final String description;

    @JsonCreator
    public static CrewAlarmType from(final String name) {
        if (crewAlarmTypeMap.containsKey(name)) {
            return crewAlarmTypeMap.get(name);
        }
        throw new AlarmException(ALARM_TYPE_NOT_FOUND, name);
    }

    @JsonValue
    public String getName() {
        return name();
    }
}
