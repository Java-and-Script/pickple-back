package kr.pickple.back.chat.repository;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.exception.ChatException;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
                select cm
                from ChatMessage cm
                where cm.senderId= :senderId and cm.type = kr.pickple.back.chat.domain.MessageType.ENTER
                order by cm.createdAt desc
                limit 1
            """)
    Optional<ChatMessage> findLastEnteringChatMessageBySenderId(final Long senderId);

    List<ChatMessage> findAllByChatRoomIdAndCreatedAtGreaterThanEqual(final Long chatRoomId,
            final LocalDateTime createdAt);

    ChatMessage findTopByChatRoomIdOrderByCreatedAtDesc(final Long chatRoomId);

    default ChatMessage getLastEnteringChatMessageBySenderId(final Long senderId) {
        return findLastEnteringChatMessageBySenderId(senderId)
                .orElseThrow(() -> new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, senderId));
    }
}
