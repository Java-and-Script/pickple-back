package kr.pickple.back.auth.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {

    AUTH_NOT_FOUND_OAUTH_PROVIDER(HttpStatus.NOT_FOUND, "AUT-001", "지원하지 않는 소셜 로그인 타입"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
