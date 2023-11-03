package kr.pickple.back.game.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameMemberCreateRequest {

    @NotNull(message = "회원 ID가 입력되지 않음")
    @Positive(message = "회원 ID는 1이상의 자연수이어야 함")
    private Long memberId;
}
