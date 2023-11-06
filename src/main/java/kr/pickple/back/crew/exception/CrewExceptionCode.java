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
    CREW_MEMBER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "CRE-005", "크루원의 상태를 찾을 수 없음"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
