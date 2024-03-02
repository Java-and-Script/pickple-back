package kr.pickple.back.chat.repository;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.exception.ChatException;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("update ChatRoom cr set cr.memberCount = :memberCount where cr.id = :chatRoomId")
    void updateMemberCount(final Long chatRoomId, final Integer memberCount);

    default ChatRoom getChatRoomById(final Long chatRoomId) {
        return findById(chatRoomId).orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND, chatRoomId));
    }
}
