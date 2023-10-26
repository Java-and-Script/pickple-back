package kr.pickple.back.game.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameStatus {

    OPEN("모집 중"),
    CLOSED("모집 마감"),
    ENDED("경기 종료"),
    ;

    private final String description;
}
