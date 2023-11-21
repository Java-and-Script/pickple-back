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
public enum Category {
    ADDRESS("location");

    private static final Map<String, Category> categoryMap = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(Category::getValue, Function.identity())));

    @JsonValue
    private final String value;

    @JsonCreator
    public static Category from(final String value) {
        if (categoryMap.containsKey(value)) {
            return categoryMap.get(value);
        }

        throw new GameException(GameExceptionCode.GAME_SEARCH_CATEGORY_IS_INVALID, value);
    }
}
