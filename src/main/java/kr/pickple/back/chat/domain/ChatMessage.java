package kr.pickple.back.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.chat.util.MessageTypeAttributeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @NotNull
    @Convert(converter = MessageTypeAttributeConverter.class)
    private MessageType type;

    @Getter
    @NotNull
    @Column(length = 500)
    private String content;

    @Getter
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Getter
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder
    private ChatMessage(final ChatRoom chatRoom, final Member sender, final String content, final MessageType type) {
        this.type = type;
        this.content = content;
        this.chatRoom = chatRoom;
        this.sender = sender;
    }

    public void updateContent(final String content) {
        this.content = content;
    }

    public Boolean isMatchedMessageType(final MessageType type) {
        return this.type == type;
    }
}
