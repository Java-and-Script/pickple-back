package kr.pickple.back.chat.domain;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import kr.pickple.back.chat.exception.ChatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoomType {
    PERSONAL("개인"),
    GAME("게스트"),
    CREW("크루"),
    ;

    private static final Map<String, RoomType> roomTypeMap = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(RoomType::getDescription, Function.identity())));

    @Getter
    @JsonValue
    private final String description;

    @JsonCreator
    public static RoomType from(final String description) {
        if (roomTypeMap.containsKey(description)) {
            return roomTypeMap.get(description);
        }

        throw new ChatException(CHAT_ROOM_TYPE_NOT_FOUND, description);
    }
}
