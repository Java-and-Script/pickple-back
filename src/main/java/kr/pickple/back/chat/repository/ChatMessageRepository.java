package kr.pickple.back.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.chat.repository.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findAllByChatRoomIdAndCreatedAtGreaterThanEqual(
            final Long chatRoomId,
            final LocalDateTime createdAt
    );

    ChatMessageEntity findTopByChatRoomIdOrderByCreatedAtDesc(final Long chatRoomId);

    @Query("""
                select cm.createdAt
                from ChatMessageEntity cm
                where cm.senderId= :memberId
                    and cm.chatRoomId = :chatRoomId
                    and cm.type = kr.pickple.back.chat.domain.MessageType.ENTER
                order by cm.createdAt desc
                limit 1
            """)
    LocalDateTime findChatRoomLastEntranceMessageCreatedAt(final Long memberId, final Long chatRoomId);
}
