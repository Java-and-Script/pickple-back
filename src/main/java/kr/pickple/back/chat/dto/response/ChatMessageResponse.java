package kr.pickple.back.chat.dto.response;

import java.time.LocalDateTime;

import kr.pickple.back.chat.domain.MessageType;
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
}
