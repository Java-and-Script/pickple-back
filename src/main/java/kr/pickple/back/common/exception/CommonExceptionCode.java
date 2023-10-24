package kr.pickple.back.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonExceptionCode implements ExceptionCode {

    COMMON_NOT_FOUND(HttpStatus.NOT_FOUND, "COM-001", "요청한 URL에 해당하는 리소스를 찾을 수 없음"),
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COM-002", "사용자 입력 유효성 검사 실패"),
    COMMON_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COM-003", "허용되지 않은 HTTP Method 요청 발생"),
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COM-004", "기타 서버 내부 에러 발생"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
