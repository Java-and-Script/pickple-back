package kr.pickple.back.game.service;

import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.util.DateTimeUtil;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.dto.request.MannerScoreReview;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.implement.GameMemberReader;
import kr.pickple.back.game.implement.GameMemberWriter;
import kr.pickple.back.game.implement.GameWriter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameReviewMannerScoresService {

    private static final int REVIEW_POSSIBLE_DAYS = 7;

    private final GameWriter gameWriter;
    private final GameMemberReader gameMemberReader;
    private final GameMemberWriter gameMemberWriter;

    @Transactional
    public void reviewMannerScores(
            final Long loggedInMemberId,
            final Long gameId,
            final List<MannerScoreReview> mannerScoreReviews
    ) {
        final GameMember gameMember = gameMemberReader.readByMemberIdAndGameId(loggedInMemberId, gameId);

        if (gameMember.isReviewDone()) {
            throw new GameException(GAME_MEMBER_NOT_ALLOWED_TO_REVIEW_AGAIN, loggedInMemberId);
        }

        final Game game = gameMember.getGame();
        final LocalDateTime gamePlayEndDateTime = game.getPlayEndDatetime();

        if (isNotReviewPeriod(gamePlayEndDateTime)) {
            throw new GameException(
                    GAME_MEMBERS_CAN_REVIEW_DURING_POSSIBLE_PERIOD,
                    game.getPlayDate(),
                    game.getPlayEndTime()
            );
        }

        gameWriter.reviewMannerScores(loggedInMemberId, gameId, mannerScoreReviews);
        gameMemberWriter.updateReviewDone(loggedInMemberId, gameId);
    }

    private Boolean isNotReviewPeriod(final LocalDateTime gamePlayEndDateTime) {
        return isBeforeThanPlayEndTime(gamePlayEndDateTime) || isAfterReviewPossibleTime(gamePlayEndDateTime);
    }

    private Boolean isBeforeThanPlayEndTime(final LocalDateTime gamePlayEndDateTime) {
        return DateTimeUtil.isAfterThanNow(gamePlayEndDateTime);
    }

    private Boolean isAfterReviewPossibleTime(final LocalDateTime gamePlayEndDateTime) {
        final LocalDateTime reviewDeadlineDatetime = gamePlayEndDateTime.plusDays(REVIEW_POSSIBLE_DAYS);

        return DateTimeUtil.isEqualOrAfter(reviewDeadlineDatetime, LocalDateTime.now());
    }
}
