package kr.pickple.back.member.exception;

import kr.pickple.back.common.exception.BusinessException;
import kr.pickple.back.common.exception.ExceptionCode;

public class MemberException extends BusinessException {

    public MemberException(ExceptionCode exceptionCode, Object... rejectedValues) {
        super(exceptionCode, rejectedValues);
    }
}
