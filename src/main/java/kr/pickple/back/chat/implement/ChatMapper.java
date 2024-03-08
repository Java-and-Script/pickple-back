package kr.pickple.back.chat.implement;

import kr.pickple.back.chat.repository.entity.ChatMessageEntity;
import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.repository.entity.ChatRoomEntity;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMapper {

    public static ChatRoom mapChatRoomEntityToDomain(final ChatRoomEntity chatRoomEntity) {
        return ChatRoom.builder()
                .chatRoomId(chatRoomEntity.getId())
                .type(chatRoomEntity.getType())
                .name(chatRoomEntity.getName())
                .memberCount(chatRoomEntity.getMemberCount())
                .maxMemberCount(chatRoomEntity.getMaxMemberCount())
                .createdAt(chatRoomEntity.getCreatedAt())
                .build();
    }

    public static ChatMessage mapChatMessageEntityToDomain(
            final ChatMessageEntity chatMessageEntity,
            final Member sender,
            final ChatRoom chatRoom
    ) {
        return ChatMessage.builder()
                .chatMessageId(chatMessageEntity.getId())
                .type(chatMessageEntity.getType())
                .content(chatMessageEntity.getContent())
                .sender(sender)
                .chatRoom(chatRoom)
                .createdAt(chatMessageEntity.getCreatedAt())
                .build();
    }
}
