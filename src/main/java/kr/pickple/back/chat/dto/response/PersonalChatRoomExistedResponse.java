package kr.pickple.back.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class PersonalChatRoomExistedResponse {

    private Long roomId;
    private Boolean isSenderActive;
}
