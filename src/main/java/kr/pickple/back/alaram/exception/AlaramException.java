package kr.pickple.back.alaram.exception;

import kr.pickple.back.common.exception.BusinessException;
import kr.pickple.back.common.exception.ExceptionCode;

public class AlaramException extends BusinessException {

    public AlaramException(final ExceptionCode exceptionCode, final Object... rejectedValues) {
        super(exceptionCode, rejectedValues);
    }
}
