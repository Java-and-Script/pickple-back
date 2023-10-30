package kr.pickple.back.address.exception;

import kr.pickple.back.common.exception.BusinessException;
import kr.pickple.back.common.exception.ExceptionCode;

public class AddressException extends BusinessException {

    public AddressException(final ExceptionCode exceptionCode, final Object... rejectedValues) {
        super(exceptionCode, rejectedValues);
    }
}
