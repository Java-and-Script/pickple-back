package kr.pickple.back.crew.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewStatus {

    OPEN("모집 중"),
    CLOSED("모집 마감"),
    ;

    private final String description;
}
