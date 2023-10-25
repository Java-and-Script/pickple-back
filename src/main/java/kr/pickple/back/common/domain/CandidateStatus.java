package kr.pickple.back.common.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CandidateStatus {

    WAITING("대기"),
    ACCEPTED("수락"),
    DENIED("거절"),
    CANCELED("취소"),
    ;

    private final String name;
}
