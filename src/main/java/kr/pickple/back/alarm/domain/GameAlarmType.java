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
public enum GameAlarmType {

    HOST_WAITING("게스트 모집 참여 수락을 기다리고 있어요"),
    GUEST_ACCEPT("게스트 참여가 수락되었어요"),
    GUEST_DENIED("게스트 참여가 거절되었어요"),
    ;

    private static final Map<String, GameAlarmType> gameAlarmTypeMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(GameAlarmType::getDescription, Function.identity())));

    private final String description;

    @JsonCreator
    public static GameAlarmType from(final String description) {
        if (gameAlarmTypeMap.containsKey(description)) {
            return gameAlarmTypeMap.get(description);
        }
        throw new AlarmException(ALARM_TYPE_NOT_FOUND, description);
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
