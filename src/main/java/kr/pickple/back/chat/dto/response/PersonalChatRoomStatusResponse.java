package kr.pickple.back.chat.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalChatRoomStatusResponse {

    private Long roomId;
    private Boolean isSenderActive;
}
