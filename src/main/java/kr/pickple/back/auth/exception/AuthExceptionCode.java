package kr.pickple.back.auth.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {

    AUTH_NOT_FOUND_OAUTH_PROVIDER(HttpStatus.NOT_FOUND, "AUT-001", "지원하지 않는 소셜 로그인 타입"),
    AUTH_EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "AUT-002", "AccessToken 토큰 만료"),
    AUTH_EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "AUT-003", "RefreshToken 토큰 만료"),
    AUTH_EXPIRED_REGISTER_TOKEN(HttpStatus.BAD_REQUEST, "AUT-004", "RegisterToken 토큰 만료"),
    AUTH_INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "AUT-005", "유효하지 않은 AccessToken"),
    AUTH_INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "AUT-006", "유효하지 않은 RefreshToken"),
    AUTH_INVALID_REGISTER_TOKEN(HttpStatus.BAD_REQUEST, "AUT-007", "유효하지 않은 RegisterToken"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
