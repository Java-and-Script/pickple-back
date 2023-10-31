package kr.pickple.back.position.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PositionExceptionCode implements ExceptionCode {

    POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "POS-001", "포지션을 찾을 수 없음"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
