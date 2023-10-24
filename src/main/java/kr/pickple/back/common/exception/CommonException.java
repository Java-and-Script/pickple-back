package kr.pickple.back.common.exception;

public class CommonException extends BusinessException {

    protected CommonException(final ExceptionCode exceptionCode, final Object... rejectedValues) {
        super(exceptionCode, rejectedValues);
    }
}
