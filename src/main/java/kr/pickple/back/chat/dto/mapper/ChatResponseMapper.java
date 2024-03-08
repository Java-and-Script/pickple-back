package kr.pickple.back.chat.dto.mapper;

import java.util.List;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.PersonalChatRoomStatus;
import kr.pickple.back.chat.dto.response.ChatMemberResponse;
import kr.pickple.back.chat.dto.response.ChatMessageResponse;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.ChatRoomResponse;
import kr.pickple.back.chat.dto.response.PersonalChatRoomStatusResponse;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatResponseMapper {

    public static ChatRoomResponse mapToChatRoomResponseDto(
            final ChatMessage lastChatMessage,
            final ChatRoomDetailResponse chatRoomDetailResponse
    ) {
        return ChatRoomResponse.builder()
                .id(chatRoomDetailResponse.getId())
                .roomName(chatRoomDetailResponse.getRoomName())
                .roomIconImageUrl(chatRoomDetailResponse.getRoomIconImageUrl())
                .type(chatRoomDetailResponse.getType())
                .memberCount(chatRoomDetailResponse.getMemberCount())
                .maxMemberCount(chatRoomDetailResponse.getMaxMemberCount())
                .playStartTime(chatRoomDetailResponse.getPlayStartTime())
                .playTimeMinutes(chatRoomDetailResponse.getPlayTimeMinutes())
                .lastMessageContent(lastChatMessage.getContent())
                .lastMessageCreatedAt(lastChatMessage.getCreatedAt())
                .createdAt(chatRoomDetailResponse.getCreatedAt())
                .build();
    }

    public static ChatRoomDetailResponse mapToPersonalChatRoomDetailResponseDto(
            final Member sender,
            final Member receiver,
            final ChatRoom chatRoom
    ) {
        final List<ChatMemberResponse> chatMemberResponses = List.of(
                mapToChatMemberResponseDto(sender),
                mapToChatMemberResponseDto(receiver)
        );

        return ChatRoomDetailResponse.builder()
                .id(chatRoom.getChatRoomId())
                .roomName(receiver.getNickname())
                .roomIconImageUrl(receiver.getProfileImageUrl())
                .type(chatRoom.getType())
                .domainId(receiver.getMemberId())
                .memberCount(chatRoom.getMemberCount())
                .maxMemberCount(chatRoom.getMaxMemberCount())
                .members(chatMemberResponses)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static ChatRoomDetailResponse mapToCrewChatRoomDetailResponseDto(
            final Crew crew,
            final ChatRoom chatRoom,
            final List<Member> members
    ) {
        final List<ChatMemberResponse> chatMemberResponses = members.stream()
                .map(ChatResponseMapper::mapToChatMemberResponseDto)
                .toList();

        return ChatRoomDetailResponse.builder()
                .id(chatRoom.getChatRoomId())
                .roomName(chatRoom.getName())
                .roomIconImageUrl(crew.getProfileImageUrl())
                .type(chatRoom.getType())
                .domainId(crew.getCrewId())
                .memberCount(chatRoom.getMemberCount())
                .maxMemberCount(chatRoom.getMaxMemberCount())
                .members(chatMemberResponses)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static ChatRoomDetailResponse mapToGameChatRoomDetailResponseDto(
            final Game game,
            final ChatRoom chatRoom,
            final List<Member> members
    ) {
        final List<ChatMemberResponse> chatMemberResponses = members.stream()
                .map(ChatResponseMapper::mapToChatMemberResponseDto)
                .toList();

        return ChatRoomDetailResponse.builder()
                .id(chatRoom.getChatRoomId())
                .roomName(chatRoom.getName())
                .type(chatRoom.getType())
                .domainId(game.getGameId())
                .memberCount(chatRoom.getMemberCount())
                .maxMemberCount(chatRoom.getMaxMemberCount())
                .playStartTime(game.getPlayStartTime())
                .playTimeMinutes(game.getPlayTimeMinutes())
                .members(chatMemberResponses)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static ChatMemberResponse mapToChatMemberResponseDto(final Member member) {
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

    public static ChatMessageResponse mapToChatMessageResponseDto(final ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .type(chatMessage.getType())
                .content(chatMessage.getContent())
                .sender(mapToChatMemberResponseDto(chatMessage.getSender()))
                .roomId(chatMessage.getChatRoom().getChatRoomId())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
