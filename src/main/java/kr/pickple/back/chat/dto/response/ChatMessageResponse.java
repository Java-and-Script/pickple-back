package kr.pickple.back.chat.dto.response;

import java.time.LocalDateTime;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.MessageType;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageResponse {

    private MessageType type;
    private String content;
    private ChatMemberResponse sender;
    private Long roomId;
    private LocalDateTime createdAt;

    public static ChatMessageResponse of(final ChatMessage chatMessage, final Member sender, final ChatRoom chatRoom) {
        return ChatMessageResponse.builder()
                .type(chatMessage.getType())
                .content(chatMessage.getContent())
                .sender(ChatMemberResponse.from(sender))
                .roomId(chatRoom.getId())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
