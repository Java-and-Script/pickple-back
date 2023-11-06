package kr.pickple.back.crew.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMemberPermitRequest {

    @NotBlank(message = "크루원 가입 상태는 필수입니다.")
    private String status;
}
