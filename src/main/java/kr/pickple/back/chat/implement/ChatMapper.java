package kr.pickple.back.chat.implement;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatMessageDomain;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.member.domain.MemberDomain;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMapper {

    public static ChatRoomDomain mapChatRoomEntityToDomain(final ChatRoom chatRoomEntity) {
        return ChatRoomDomain.builder()
                .chatRoomId(chatRoomEntity.getId())
                .type(chatRoomEntity.getType())
                .name(chatRoomEntity.getName())
                .memberCount(chatRoomEntity.getMemberCount())
                .maxMemberCount(chatRoomEntity.getMaxMemberCount())
                .createdAt(chatRoomEntity.getCreatedAt())
                .build();
    }

    public static ChatMessageDomain mapChatMessageEntityToDomain(
            final ChatMessage chatMessageEntity,
            final MemberDomain sender,
            final ChatRoomDomain chatRoom
    ) {
        return ChatMessageDomain.builder()
                .chatMessageId(chatMessageEntity.getId())
                .type(chatMessageEntity.getType())
                .content(chatMessageEntity.getContent())
                .sender(sender)
                .chatRoom(chatRoom)
                .createdAt(chatMessageEntity.getCreatedAt())
                .build();
    }
}
