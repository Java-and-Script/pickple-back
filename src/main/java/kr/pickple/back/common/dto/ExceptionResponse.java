package kr.pickple.back.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionResponse {

    private final String code;
    private final String message;
    private final Object[] rejectedValues;
}
