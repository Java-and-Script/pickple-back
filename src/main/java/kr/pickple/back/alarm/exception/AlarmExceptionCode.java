package kr.pickple.back.alarm.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmExceptionCode implements ExceptionCode {

    ALARM_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "ALA-001", "알람 읽음 여부를 찾을 수 없음"),
    ALARM_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "ALA-002", "알림 타입을 찾을 수 없음"),
    ALARM_EXISTS_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "ALA-003", "사용자의 알람 상태 여부를 찾을 수 없음"),
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "ALA-004", "해당 알람을 찾을 수 없음"),
    ALARM_CONVERT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "ALA-005", "알람 타입 변환 중 오류 발생"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
