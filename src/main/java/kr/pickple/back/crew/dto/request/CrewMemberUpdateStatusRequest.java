package kr.pickple.back.crew.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.RegistrationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMemberUpdateStatusRequest {

    @NotNull(message = "크루원 가입 상태는 필수입니다.")
    private RegistrationStatus status;
}
