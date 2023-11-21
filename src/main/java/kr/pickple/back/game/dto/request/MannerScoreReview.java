package kr.pickple.back.game.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class MannerScoreReview {

    @NotNull(message = "회원 ID가 입력되지 않음")
    @Positive(message = "회원 ID는 1이상의 자연수로 입력")
    private Long memberId;

    @NotNull(message = "리뷰에 사용되는 매너 스코어가 입력되지 않음")
    @Min(value = -1, message = "리뷰에 사용되는 매너 스코어는 -1보다 작을 수 없음")
    @Max(value = 1, message = "리뷰에 사용되는 매너 스코어는 1보다 클 수 없음")
    private Integer mannerScore;
}
