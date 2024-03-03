package kr.pickple.back.chat.domain;

import java.time.LocalDateTime;

import kr.pickple.back.member.domain.MemberDomain;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageDomain {

    private Long chatMessageId;
    private MessageType type;
    private String content;
    private MemberDomain sender;
    private ChatRoomDomain chatRoom;
    private LocalDateTime createdAt;
}
