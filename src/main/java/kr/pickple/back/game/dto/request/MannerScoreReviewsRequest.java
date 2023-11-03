package kr.pickple.back.game.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MannerScoreReviewsRequest {

    @Valid
    @NotNull(message = "매너 스코어 리뷰 목록이 입력되지 않음")
    private List<MannerScoreReview> mannerScoreReviews;
}
