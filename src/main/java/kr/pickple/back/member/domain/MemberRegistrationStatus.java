package kr.pickple.back.member.domain;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import kr.pickple.back.member.exception.MemberException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRegistrationStatus {

    NONE("없음"),
    WAITING("대기"),
    CONFIRMED("확정");

    private static final Map<String, MemberRegistrationStatus> memberRegistrationStatusMap = Collections.unmodifiableMap(
            Stream.of(values())
                    .collect(Collectors.toMap(MemberRegistrationStatus::getDescription, Function.identity())));

    @JsonValue
    private final String description;

    @JsonCreator
    public static MemberRegistrationStatus from(final String description) {
        if (memberRegistrationStatusMap.containsKey(description)) {
            return memberRegistrationStatusMap.get(description);
        }

        throw new MemberException(MEMBER_STATUS_NOT_FOUND, description);
    }
}
