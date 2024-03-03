package kr.pickple.back.chat.domain;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.time.LocalDateTime;

import kr.pickple.back.chat.exception.ChatException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDomain {

    private Long chatRoomId;
    private String name;
    private RoomType type;
    private Integer memberCount;
    private Integer maxMemberCount;
    private LocalDateTime createdAt;

    public void increaseMemberCount() {
        if (memberCount.equals(maxMemberCount)) {
            throw new ChatException(CHAT_ROOM_IS_FULL, memberCount);
        }

        memberCount += 1;
    }

    public void decreaseMemberCount() {
        if (isEmpty()) {
            throw new ChatException(CHAT_ROOM_IS_EMPTY, memberCount);
        }

        memberCount -= 1;
    }

    public Boolean isEmpty() {
        return memberCount == 0;
    }

    public Boolean isMatchedRoomType(final RoomType type) {
        return this.type == type;
    }
}
