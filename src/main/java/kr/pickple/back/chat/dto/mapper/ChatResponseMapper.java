package kr.pickple.back.chat.dto.mapper;

import java.util.List;

import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.PersonalChatRoomStatus;
import kr.pickple.back.chat.dto.response.ChatMemberResponse;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.PersonalChatRoomStatusResponse;
import kr.pickple.back.member.domain.MemberDomain;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatResponseMapper {

    public static ChatRoomDetailResponse mapToPersonalChatRoomDetailResponseDto(
            final MemberDomain sender,
            final MemberDomain receiver,
            final ChatRoomDomain chatRoom
    ) {
        return ChatRoomDetailResponse.builder()
                .id(chatRoom.getChatRoomId())
                .roomName(receiver.getNickname())
                .roomIconImageUrl(receiver.getProfileImageUrl())
                .type(chatRoom.getType())
                .domainId(receiver.getMemberId())
                .memberCount(chatRoom.getMemberCount())
                .maxMemberCount(chatRoom.getMaxMemberCount())
                .members(List.of(mapToChatMemberResponseDto(sender), mapToChatMemberResponseDto(receiver)))
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static ChatMemberResponse mapToChatMemberResponseDto(final MemberDomain member) {
        return ChatMemberResponse.builder()
                .id(member.getMemberId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }

    public static PersonalChatRoomStatusResponse mapToPersonalChatRoomStatusResponseDto(
            final PersonalChatRoomStatus personalChatRoomStatus
    ) {
        return PersonalChatRoomStatusResponse.builder()
                .roomId(personalChatRoomStatus.getRoomId())
                .isSenderActive(personalChatRoomStatus.getIsSenderActive())
                .build();
    }
}
