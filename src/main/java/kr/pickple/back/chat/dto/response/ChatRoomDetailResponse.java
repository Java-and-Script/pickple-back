package kr.pickple.back.chat.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDetailResponse {

    private Long id;
    private String roomName;
    private String roomIconImageUrl;
    private RoomType type;
    private Long domainId;
    private Integer memberCount;
    private Integer maxMemberCount;
    private LocalTime playStartTime;
    private Integer playTimeMinutes;
    private List<ChatMemberResponse> members;
    private LocalDateTime createdAt;

    public static ChatRoomDetailResponse of(
            final ChatRoom chatRoom,
            final Member receiver,
            final List<ChatMemberResponse> chatMemberResponses
    ) {
        return ChatRoomDetailResponse.builder()
                .id(chatRoom.getId())
                .roomName(receiver.getNickname())
                .roomIconImageUrl(receiver.getProfileImageUrl())
                .type(chatRoom.getType())
                .domainId(receiver.getId())
                .memberCount(chatRoom.getMemberCount())
                .maxMemberCount(chatRoom.getMaxMemberCount())
                .members(chatMemberResponses)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static ChatRoomDetailResponse of(
            final ChatRoom chatRoom,
            final Game game,
            final List<ChatMemberResponse> chatMemberResponses
    ) {
        return ChatRoomDetailResponse.builder()
                .id(chatRoom.getId())
                .roomName(chatRoom.getName())
                .type(chatRoom.getType())
                .domainId(game.getId())
                .memberCount(chatRoom.getMemberCount())
                .maxMemberCount(chatRoom.getMaxMemberCount())
                .playStartTime(game.getPlayStartTime())
                .playTimeMinutes(game.getPlayTimeMinutes())
                .members(chatMemberResponses)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    public static ChatRoomDetailResponse of(
            final ChatRoom chatRoom,
            final Crew crew,
            final List<ChatMemberResponse> chatMemberResponses
    ) {
        return ChatRoomDetailResponse.builder()
                .id(chatRoom.getId())
                .roomName(chatRoom.getName())
                .roomIconImageUrl(crew.getProfileImageUrl())
                .type(chatRoom.getType())
                .domainId(crew.getId())
                .memberCount(chatRoom.getMemberCount())
                .maxMemberCount(chatRoom.getMaxMemberCount())
                .members(chatMemberResponses)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
