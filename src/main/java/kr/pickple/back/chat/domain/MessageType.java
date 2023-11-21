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
public enum MessageType {

    ENTER("입장"),
    TALK("대화"),
    LEAVE("퇴장"),
    ;

    private static final String ENTER_MESSAGE_SUFFIX = "님이 채팅방에 입장하셨습니다.";
    private static final String LEAVE_MESSAGE_SUFFIX = "님이 채팅방을 떠났습니다.";
    private static final Map<String, MessageType> messageTypeMap = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(MessageType::getDescription, Function.identity())));

    @Getter
    @JsonValue
    private final String description;

    @JsonCreator
    public static MessageType from(final String description) {
        if (messageTypeMap.containsKey(description)) {
            return messageTypeMap.get(description);
        }

        throw new ChatException(CHAT_MESSAGE_TYPE_NOT_FOUND, description);
    }

    public static String makeEnterMessage(final String nickname) {
        return nickname + ENTER_MESSAGE_SUFFIX;
    }

    public static String makeLeaveMessage(final String nickname) {
        return nickname + LEAVE_MESSAGE_SUFFIX;
    }
}
