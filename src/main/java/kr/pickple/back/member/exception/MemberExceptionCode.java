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
    MEMBER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM-003", "사용자 상태는 활동이거나 탈퇴만 가능");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
