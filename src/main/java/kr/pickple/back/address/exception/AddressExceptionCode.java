package kr.pickple.back.address.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AddressExceptionCode implements ExceptionCode {

    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADD-001", "입력한 주소에 해당하는 리소스를 찾을 수 없음"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
