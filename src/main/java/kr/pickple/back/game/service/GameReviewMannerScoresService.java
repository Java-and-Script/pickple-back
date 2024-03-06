package kr.pickple.back.game.service;

import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.util.DateTimeUtil;
import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.dto.request.MannerScoreReview;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.implement.GameMemberReader;
import kr.pickple.back.game.implement.GameMemberWriter;
import kr.pickple.back.game.implement.GameReader;
import kr.pickple.back.game.implement.GameWriter;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameReviewMannerScoresService {

    private static final int REVIEW_POSSIBLE_DAYS = 7;

    private final GameMemberRepository gameMemberRepository;
    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final GameMemberReader gameMemberReader;
    private final GameReader gameReader;
    private final MemberReader memberReader;
    private final GameWriter gameWriter;
    private final GameMemberWriter gameMemberWriter;

    @Transactional
    public void reviewMannerScores(
            final Long loggedInMemberId,
            final Long gameId,
            final List<MannerScoreReview> mannerScoreReviews
    ) {
        final GameMember gameMember = gameMemberReader.readGameMemberByMemberIdAndGameId(loggedInMemberId, gameId);

        if (gameMember.isAlreadyReviewDone()) {
            throw new GameException(GAME_MEMBER_NOT_ALLOWED_TO_REVIEW_AGAIN, loggedInMemberId);
        }

        // --

        final GameDomain gameDomain = gameReader.read(gameId);
        if (isNotReviewPeriod(gameDomain)) {
            throw new GameException(GAME_MEMBERS_CAN_REVIEW_DURING_POSSIBLE_PERIOD, gameDomain.getPlayDate(),
                    gameDomain.getPlayEndTime());
        }

        // --
        gameWriter.reviewMannerScores(loggedInMemberId, gameId, mannerScoreReviews);
        gameMemberWriter.updateReviewDone(loggedInMemberId, gameId);
    }

    private Boolean isNotReviewPeriod(final GameDomain gameDomain) {
        return isBeforeThanPlayEndTime(gameDomain) || isAfterReviewPossibleTime(gameDomain);
    }

    private Boolean isBeforeThanPlayEndTime(final GameDomain gameDomain) {
        return DateTimeUtil.isAfterThanNow(gameDomain.getPlayEndDatetime());
    }

    private Boolean isAfterReviewPossibleTime(final GameDomain gameDomain) {
        final LocalDateTime reviewDeadlineDatetime = gameDomain.getPlayEndDatetime().plusDays(REVIEW_POSSIBLE_DAYS);

        return DateTimeUtil.isEqualOrAfter(reviewDeadlineDatetime, LocalDateTime.now());
    }
}
