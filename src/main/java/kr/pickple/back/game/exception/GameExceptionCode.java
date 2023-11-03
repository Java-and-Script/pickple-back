package kr.pickple.back.game.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameExceptionCode implements ExceptionCode {

    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "GAM-001", "게스트 모집글을 찾을 수 없음"),
    GAME_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST, "GAM-002", "게스트 모집글 상태는 모집 중, 모집 마감, 경기 종료만 가능"),
    GAME_MEMBER_IS_EXISTED(HttpStatus.BAD_REQUEST, "GAM-003", "이미 해당 게스트 모집글에 참여 신청한 사용자"),
    GAME_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "GAM-004", "게스트 모집글에 참여 신청된 혹은 확정된 사용자들 중에서 해당 사용자를 찾을 수 없음"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
