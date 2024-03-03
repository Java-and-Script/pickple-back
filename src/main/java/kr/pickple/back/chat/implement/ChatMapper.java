package kr.pickple.back.chat.implement;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
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
}
