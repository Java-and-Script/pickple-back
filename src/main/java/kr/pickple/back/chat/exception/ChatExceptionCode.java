package kr.pickple.back.chat.exception;

import org.springframework.http.HttpStatus;

import kr.pickple.back.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatExceptionCode implements ExceptionCode {

    CHAT_MESSAGE_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHT-001", "채팅 메시지 타입을 찾을 수 없음"),
    CHAT_ROOM_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "CHT-002", "채팅방 타입을 찾을 수 없음"),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHT-003", "채팅방을 찾을 수 없음"),
    CHAT_ROOM_IS_FULL(HttpStatus.BAD_REQUEST, "CHT-004", "채팅방 인원이 가득참"),
    CHAT_ROOM_IS_EMPTY(HttpStatus.NOT_FOUND, "CHT-005", "채팅방 인원이 비었음"),
    CHAT_MEMBER_IS_ALREADY_IN_ROOM(HttpStatus.BAD_REQUEST, "CHT-006", "해당 채팅방에 이미 존재하는 사용자"),
    CHAT_MEMBER_IS_NOT_IN_ROOM(HttpStatus.BAD_REQUEST, "CHT-007", "해당 채팅방에 존재하지 않는 사용자"),
    CHAT_MEMBER_CANNOT_CHAT_SELF(HttpStatus.BAD_REQUEST, "CHT-008", "사용자는 자기 자신과 채팅할 수 없음"),
    CHAT_MAX_MEMBER_COUNT_SHOULD_BE_BIGGER_THAN_MEMBER_COUNT(HttpStatus.BAD_REQUEST, "CHT-009", "채팅방 인원제한은 현재 인원수 이상이어야함"),
    CHAT_CREW_NOT_FOUND(HttpStatus.NOT_FOUND, "CHT-010", "해당 채팅방을 소유한 크루 정보를 찾을 수 없음"),
    CHAT_GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "CHT-011", "해당 채팅방을 소유한 게스트 모집글 정보를 찾을 수 없음"),
    CHAT_RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHT-012", "해당 1:1 채팅방에 나를 제외한 사용자가 존재하지 않음"),
    CHAT_CREW_CHATROOM_NOT_ALLOWED_TO_LEAVE(HttpStatus.BAD_REQUEST, "CHT-013", "크루 탈퇴 전에는 크루 채팅방을 떠날 수 없음"),
    CHAT_GAME_CHATROOM_NOT_ALLOWED_TO_LEAVE(HttpStatus.BAD_REQUEST, "CHT-014", "경기 종료 전에는 게스트 모집글 채팅방을 떠날 수 없음"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
