package kr.pickple.back.crew.exception;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CrewExceptionCode implements ExceptionCode {

    CREW_NOT_FOUND(HttpStatus.NOT_FOUND, "CRE-001", "크루를 찾을 수 없음"),
    CREW_IS_EXISTED(HttpStatus.BAD_REQUEST, "CRE-002", "이미 존재하는 크루 정보"),
    CREW_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "CRE-003", "크루의 상태는 모집 중이거나 모집 마감만 가능"),
    CREW_MEMBER_ALREADY_EXISTED(HttpStatus.BAD_REQUEST, "CRE-004", "이미 가입 신청이 되거나 가입된 크루원"),
    CREW_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "CRE-005", "해당 크루원을 찾을 수 없음"),
    CREW_CAPACITY_LIMIT_REACHED(HttpStatus.BAD_REQUEST, "CRE-006", "해당 크루의 정원을 초과할 수 없음"),
    CREW_STATUS_IS_CLOSED(HttpStatus.BAD_REQUEST, "CRE-007", "해당 크루는 모집중이 아님"),
    CREW_IS_NOT_LEADER(HttpStatus.BAD_REQUEST, "CRE-008", "해당 크루의 크루장이 아님"),
    CREW_MEMBER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "CRE-009", "해당 사용자는 크루 가입 신청을 거절하거나 취소할 권한 없음"),
    CREW_MEMBER_NOT_APPLIED(HttpStatus.BAD_REQUEST, "CRE-010", "가입 신청을 한 상태가 아니므로, 가입 신청을 취소할 수 없음"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
