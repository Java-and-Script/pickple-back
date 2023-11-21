package kr.pickple.back.crew.domain;

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

import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_STATUS_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum CrewStatus {

    OPEN("모집 중"),
    CLOSED("모집 마감"),
    ;

    private static final Map<String, CrewStatus> crewStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(CrewStatus::getDescription, Function.identity())));

    @JsonValue
    private final String description;

    @JsonCreator
    public static CrewStatus from(final String description) {
        if (crewStatusMap.containsKey(description)) {
            return crewStatusMap.get(description);
        }
        throw new CrewException(CREW_STATUS_NOT_FOUND, description);
    }
}
