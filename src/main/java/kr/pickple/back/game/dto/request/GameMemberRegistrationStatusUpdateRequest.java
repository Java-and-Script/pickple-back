package kr.pickple.back.game.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.RegistrationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMemberRegistrationStatusUpdateRequest {

    @NotNull(message = "게스트 모집글에 참여 신청한 사용자에 대한 상태가 입력되지 않음")
    private RegistrationStatus status;
}
