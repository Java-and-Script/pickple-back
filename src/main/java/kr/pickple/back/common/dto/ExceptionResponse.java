package kr.pickple.back.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ExceptionResponse {

    private final String code;
    private final String message;
    private final Object[] rejectedValues;

    @Builder
    protected ExceptionResponse(String code, String message, Object[] rejectedValues) {
        this.code = code;
        this.message = message;
        this.rejectedValues = rejectedValues;
    }
}
