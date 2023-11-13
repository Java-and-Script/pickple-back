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
    GAME_POSITIONS_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "GAM-005", "게스트 모집글의 포지션 목록에 중복이 존재함"),
    GAME_SEARCH_CATEGORY_IS_INVALID(HttpStatus.BAD_REQUEST, "GAM-006", "게스트 모집글의 검색 카테고리 키워드가 유효하지 않음"),
    GAME_MEMBER_IS_NOT_HOST(HttpStatus.FORBIDDEN, "GAM-007", "해당 게스트 모집글의 호스트 권한이 필요함"),
    GAME_HOST_CANNOT_BE_DELETED(HttpStatus.BAD_REQUEST, "GAM-008", "호스트는 자신의 게스트 모집글에서 삭제될 수 없음"),
    GAME_MEMBER_STATUS_IS_NOT_WAITING(HttpStatus.BAD_REQUEST, "GAM-009", "해당 게스트 모집글에 참여 신청 대기 상태가 아니라면, 참여 신청을 취소할 수 없음"),
    GAME_NOT_ALLOWED_TO_DELETE_GAME_MEMBER(HttpStatus.FORBIDDEN, "GAM-010", "해당 사용자의 게스트 모집글 참여 신청을 거절 혹은 취소할 권한이 필요함"),
    GAME_MEMBERS_CAN_REVIEW_AFTER_PLAYING(HttpStatus.BAD_REQUEST, "GAM-011", "경기 종료 이전에는 리뷰를 남길 수 없음"),
    GAME_MEMBER_CANNOT_REVIEW_SELF(HttpStatus.BAD_REQUEST, "GAM-012", "자기 자신에게 리뷰를 남길 수 없음"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
