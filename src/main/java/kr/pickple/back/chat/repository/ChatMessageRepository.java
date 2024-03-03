package kr.pickple.back.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.chat.domain.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoomIdAndCreatedAtGreaterThanEqual(
            final Long chatRoomId,
            final LocalDateTime createdAt
    );

    ChatMessage findTopByChatRoomIdOrderByCreatedAtDesc(final Long chatRoomId);

    @Query("""
                select cm.createdAt
                from ChatMessage cm
                where cm.senderId= :memberId and cm.type = kr.pickple.back.chat.domain.MessageType.ENTER
                order by cm.createdAt desc
                limit 1
            """)
    LocalDateTime findLastEntranceDatetimeByMemberId(final Long memberId);
}
