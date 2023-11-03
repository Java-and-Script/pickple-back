package kr.pickple.back.common.domain;

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

import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_MEMBER_STATUS_NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum RegistrationStatus {

    WAITING("대기"),
    CONFIRMED("확정"),
    ;

    private static final Map<String, RegistrationStatus> registrationStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(RegistrationStatus::getDescription, Function.identity())));

    @JsonValue
    private final String description;

    @JsonCreator
    public static RegistrationStatus from(final String description) {
        if (registrationStatusMap.containsKey(description)) {
            return registrationStatusMap.get(description);
        }
        throw new CrewException(CREW_MEMBER_STATUS_NOT_FOUND, description);
    }
}
