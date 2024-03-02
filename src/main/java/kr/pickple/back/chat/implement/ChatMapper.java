package kr.pickple.back.chat.implement;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.RoomType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMapper {

    public static ChatRoom mapToChatRoomEntity(final RoomType type, final String name) {
        return ChatRoom.builder()
                .type(type)
                .name(name)
                .build();
    }

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
