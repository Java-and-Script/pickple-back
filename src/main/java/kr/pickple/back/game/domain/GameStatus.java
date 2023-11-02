package kr.pickple.back.game.domain;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.exception.GameExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameStatus {

    OPEN("모집 중"),
    CLOSED("모집 마감"),
    ENDED("경기 종료"),
    ;

    private static final Map<String, GameStatus> gameStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(GameStatus::getDescription, Function.identity())));

    @JsonValue
    private final String description;

    @JsonCreator
    public static GameStatus from(final String description) {
        if (gameStatusMap.containsKey(description)) {
            return gameStatusMap.get(description);
        }

        throw new GameException(GameExceptionCode.GAME_STATUS_NOT_FOUND, description);
    }
}
