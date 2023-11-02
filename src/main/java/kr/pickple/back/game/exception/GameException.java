package kr.pickple.back.game.exception;

import kr.pickple.back.common.exception.BusinessException;
import kr.pickple.back.common.exception.ExceptionCode;

public class GameException extends BusinessException {

    public GameException(final ExceptionCode exceptionCode, final Object... rejectedValues) {
        super(exceptionCode, rejectedValues);
    }
}
