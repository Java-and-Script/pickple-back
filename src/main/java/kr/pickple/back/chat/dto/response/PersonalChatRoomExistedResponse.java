package kr.pickple.back.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class PersonalChatRoomExistedResponse {

    private Boolean isRoomExisted;
    private Boolean isSenderActive;
}
