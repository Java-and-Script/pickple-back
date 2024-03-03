package kr.pickple.back.chat.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import kr.pickple.back.chat.domain.RoomType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomResponse {

    private Long id;
    private String roomName;
    private String roomIconImageUrl;
    private RoomType type;
    private Integer memberCount;
    private Integer maxMemberCount;
    private LocalTime playStartTime;
    private Integer playTimeMinutes;
    private String lastMessageContent;
    private LocalDateTime lastMessageCreatedAt;
    private LocalDateTime createdAt;
}
