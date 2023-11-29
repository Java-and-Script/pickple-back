package kr.pickple.back.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalChatRoomCreateRequest {

    @NotNull(message = "수신자 ID가 입력되지 않음")
    @Positive(message = "수신자 ID는 1이상의 자연수로 입력해야함")
    private Long receiverId;
}
