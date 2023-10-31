package kr.pickple.back.member.domain;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.pickple.back.member.exception.MemberException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

    ACTIVE("활동"),
    WITHDRAWN("탈퇴"),
    ;

    private static final Map<String, MemberStatus> memberStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(MemberStatus::getDescription, Function.identity())));

    @JsonValue
    private final String description;

    @JsonCreator
    public static MemberStatus from(final String description) {
        if (memberStatusMap.containsKey(description)) {
            return memberStatusMap.get(description);
        }

        throw new MemberException(MEMBER_STATUS_NOT_FOUND, description);
    }

    @Converter
    public static final class MemberStatusConverter implements AttributeConverter<MemberStatus, String> {

        @Override
        public String convertToDatabaseColumn(final MemberStatus memberStatus) {
            return memberStatus.getDescription();
        }

        @Override
        public MemberStatus convertToEntityAttribute(final String description) {
            return MemberStatus.from(description);
        }
    }
}
