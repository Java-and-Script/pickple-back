package kr.pickple.back.alaram.exception;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AlaramExceptionCode implements ExceptionCode {

    ALARM_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "AEC-001", "알람 상태 여부를 찾을 수 없음"),
    ALARM_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND,"AEC-002","알림 타입을 찾을 수 없음"),
    ;


    private final HttpStatus status;
    private final String code;
    private final String message;
}