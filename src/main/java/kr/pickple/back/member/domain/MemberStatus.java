package kr.pickple.back.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

    ACTIVE("활동"),
    WITHDRAWN("탈퇴"),
    ;

    private final String name;
}
