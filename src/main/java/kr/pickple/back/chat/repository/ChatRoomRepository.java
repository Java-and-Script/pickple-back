package kr.pickple.back.chat.repository;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.exception.ChatException;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    default ChatRoom getChatRoomById(final Long chatRoomId) {
        return findById(chatRoomId).orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND, chatRoomId));
    }
}
