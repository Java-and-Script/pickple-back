package kr.pickple.back.chat.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.chat.domain.MessageType;
import kr.pickple.back.chat.util.MessageTypeAttributeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Convert(converter = MessageTypeAttributeConverter.class)
    private MessageType type;

    @NotNull
    @Column(length = 500)
    private String content;

    @NotNull
    private Long senderId;

    @NotNull
    private Long chatRoomId;

    @Builder
    private ChatMessageEntity(final MessageType type, final String content, final Long senderId,
            final Long chatRoomId) {
        this.type = type;
        this.content = content;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
    }
}
