package kr.pickple.back.common.exception;

import org.springframework.http.HttpStatus;

interface ExceptionCode {

    HttpStatus getStatus();

    String getCode();

    String getMessage();
}
