package kr.pickple.back.crew.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewExceptionCode implements ExceptionCode {

    CREW_NOT_FOUND(HttpStatus.NOT_FOUND,"CRE-001","크루를 찾을 수 없음"),
    CREW_IS_EXISTED(HttpStatus.BAD_REQUEST, "CRE-002", "이미 존재하는 크루 정보");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
