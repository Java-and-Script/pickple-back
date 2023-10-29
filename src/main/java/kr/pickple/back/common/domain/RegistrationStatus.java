package kr.pickple.back.common.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegistrationStatus {

    WAITING("대기"),
    CONFIRMED("확정"),
    ;

    private final String description;
}
