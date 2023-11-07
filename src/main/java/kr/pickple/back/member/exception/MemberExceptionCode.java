package kr.pickple.back.member.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM-001", "사용자를 찾을 수 없음"),
    MEMBER_IS_EXISTED(HttpStatus.BAD_REQUEST, "MEM-002", "이미 존재하는 사용자 정보"),
    MEMBER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM-003", "사용자 상태는 활동이거나 탈퇴만 가능"),
    MEMBER_SIGNUP_OAUTH_SUBJECT_INVALID(HttpStatus.BAD_REQUEST, "MEM-004",
            "회원 가입 시, 사용자의 OAuth ID와 Provider의 정보가 유효하지 않음"),
    MEMBER_UPDATING_MANNER_SCORE_POINT_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "MEM-005", "매너 스코어 변경 포인트가 범위를 벗어남"),
    MEMBER_POSITIONS_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "MEM-006", "사용자의 포지션 목록에 중복이 존재함");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
