package kr.pickple.back.crew.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(staticName = "to")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewApplyRequest {

    @NotNull(message = "해당 크루의 ID는 필수입니다.")
    private Long memberId;
}
