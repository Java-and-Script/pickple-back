package kr.pickple.back.fixture.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.request.MannerScoreReview;
import kr.pickple.back.game.dto.request.MannerScoreReviewsRequest;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.position.domain.Position;

public class GameDtoFixtures {

    public static GameCreateRequest gameCreateRequestBuild() {
        return GameCreateRequest.builder()
                .content("재밌는 농구 경기 해요~! 다 초보입니다")
                .playDate(LocalDate.of(2023, 2, 1))
                .playStartTime(LocalTime.of(11, 30))
                .playTimeMinutes(90)
                .mainAddress("서울 영등포구 도림동 254")
                .detailAddress("영등포 다목적 체육관 2층 201호")
                .cost(100)
                .maxMemberCount(5)
                .positions(List.of(Position.CENTER, Position.POINT_GUARD))
                .build();
    }

    public static GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequestBuild(
            final RegistrationStatus status
    ) {
        return GameMemberRegistrationStatusUpdateRequest.builder()
                .status(status)
                .build();
    }

    public static MannerScoreReviewsRequest mannerScoreReviewsRequestBuild(final List<Member> members) {
        final List<MannerScoreReview> mannerScoreReviews = members.stream()
                .map(member -> MannerScoreReview.builder()
                        .memberId(member.getId())
                        .mannerScore(1)
                        .build())
                .toList();

        return MannerScoreReviewsRequest.builder()
                .mannerScoreReviews(mannerScoreReviews)
                .build();
    }
}
