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

import static kr.pickple.back.alaram.exception.AlarmExceptionCode.ALARM_TYPE_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum AlarmType {

    CREW_LEADER_WAITING("가입 수락을 기다리고 있어요"),
    CREW_ACCEPT("크루 가입이 수락되었어요"),
    CREW_DENIED("크루 가입이 거절되었어요"),
    HOST_WAITING("게스트 모집 창여 수락이 기다리고 있어요"),
    GUEST_ACCEPT("게스트 참여가 수락되었어요"),
    GUEST_DENIED("게스트 참여가 거절되었어요"),
    ;

    private static final Map<String, AlarmType> alarmTypeMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(AlarmType::name, Function.identity())));

    private final String description;

    @JsonCreator
    public static AlarmType from(final String name) {
        if (alarmTypeMap.containsKey(name)) {
            return alarmTypeMap.get(name);
        }
        throw new CrewException(ALARM_TYPE_NOT_FOUND, name);
    }

    @JsonValue
    public String getName() {
        return name();
    }
}
