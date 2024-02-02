package kr.pickple.back.game.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
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
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameReviewMannerScoresService {

    private static final int REVIEW_POSSIBLE_DAYS = 7;

    private final GameMemberRepository gameMemberRepository;

    @Transactional
    public void reviewMannerScores(
            final Long loggedInMemberId,
            final Long gameId,
            final List<MannerScoreReview> mannerScoreReviews
    ) {
        final GameMember gameMember = gameMemberRepository.findByMemberIdAndGameIdAndStatus(
                        loggedInMemberId,
                        gameId,
                        CONFIRMED
                )
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, loggedInMemberId));

        if (gameMember.isAlreadyReviewDone()) {
            throw new GameException(GAME_MEMBER_NOT_ALLOWED_TO_REVIEW_AGAIN, loggedInMemberId);
        }

        final Game game = gameMember.getGame();
        final Member loggedInMember = gameMember.getMember();

        if (isNotReviewPeriod(game)) {
            throw new GameException(GAME_MEMBERS_CAN_REVIEW_DURING_POSSIBLE_PERIOD, game.getPlayDate(),
                    game.getPlayEndTime());
        }

        mannerScoreReviews.forEach(review -> {
            final Member reviewedMember = getReviewedMember(game, review.getMemberId());
            validateIsSelfReview(loggedInMember, reviewedMember);
            reviewedMember.updateMannerScore(review.getMannerScore());
        });

        gameMember.updateReviewDone();
    }

    private void validateIsSelfReview(final Member loggedInMember, final Member reviewedMember) {
        if (loggedInMember.equals(reviewedMember)) {
            throw new GameException(GAME_MEMBER_CANNOT_REVIEW_SELF, loggedInMember.getId(), reviewedMember.getId());
        }
    }

    private Boolean isNotReviewPeriod(final Game game) {
        return isBeforeThanPlayEndTime(game) || isAfterReviewPossibleTime(game);
    }

    private Boolean isBeforeThanPlayEndTime(final Game game) {
        return DateTimeUtil.isAfterThanNow(game.getPlayEndDatetime());
    }

    private Boolean isAfterReviewPossibleTime(final Game game) {
        final LocalDateTime reviewDeadlineDatetime = game.getPlayEndDatetime().plusDays(REVIEW_POSSIBLE_DAYS);

        return DateTimeUtil.isEqualOrAfter(reviewDeadlineDatetime, LocalDateTime.now());
    }

    private Member getReviewedMember(final Game game, final Long reviewedMemberId) {
        return getConfirmedMembers(game)
                .stream()
                .filter(confirmedMember -> confirmedMember.getId() == reviewedMemberId)
                .findFirst()
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, reviewedMemberId));
    }

    private List<Member> getConfirmedMembers(Game game) {
        return gameMemberRepository.findAllByGameIdAndStatus(game.getId(), CONFIRMED)
                .stream()
                .map(GameMember::getMember)
                .toList();
    }
}
