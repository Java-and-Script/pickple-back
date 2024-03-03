package kr.pickple.back.chat.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalChatRoomStatus {

    private Long roomId;
    private Boolean isSenderActive;
}
