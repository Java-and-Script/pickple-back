package kr.pickple.back.crew.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewApplyRequest {

    @NotNull(message = "해당 크루에 가입하려는 사용자의 ID는 필수입니다.")
    private Long memberId;
}
