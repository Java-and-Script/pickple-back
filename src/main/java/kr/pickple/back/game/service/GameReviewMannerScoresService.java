package kr.pickple.back.game.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.util.DateTimeUtil;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.game.dto.request.MannerScoreReview;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
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

    @Transactional
    public void reviewMannerScores(
            final Long loggedInMemberId,
            final Long gameId,
            final List<MannerScoreReview> mannerScoreReviews
    ) {
        final GameMemberEntity gameMemberEntity = gameMemberRepository.findByMemberIdAndGameIdAndStatus(
                        loggedInMemberId,
                        gameId,
                        CONFIRMED
                )
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, loggedInMemberId));

        if (gameMemberEntity.isAlreadyReviewDone()) {
            throw new GameException(GAME_MEMBER_NOT_ALLOWED_TO_REVIEW_AGAIN, loggedInMemberId);
        }

        final GameEntity gameEntity = gameRepository.getGameById(gameMemberEntity.getGameId());
        final Member loggedInMember = memberRepository.getMemberById(gameMemberEntity.getMemberId());

        if (isNotReviewPeriod(gameEntity)) {
            throw new GameException(GAME_MEMBERS_CAN_REVIEW_DURING_POSSIBLE_PERIOD, gameEntity.getPlayDate(),
                    gameEntity.getPlayEndTime());
        }

        mannerScoreReviews.forEach(review -> {
            final Member reviewedMember = getReviewedMember(gameEntity, review.getMemberId());
            validateIsSelfReview(loggedInMember, reviewedMember);
            reviewedMember.updateMannerScore(review.getMannerScore());
        });

        gameMemberEntity.updateReviewDone();
    }

    private void validateIsSelfReview(final Member loggedInMember, final Member reviewedMember) {
        if (loggedInMember.equals(reviewedMember)) {
            throw new GameException(GAME_MEMBER_CANNOT_REVIEW_SELF, loggedInMember.getId(), reviewedMember.getId());
        }
    }

    private Boolean isNotReviewPeriod(final GameEntity gameEntity) {
        return isBeforeThanPlayEndTime(gameEntity) || isAfterReviewPossibleTime(gameEntity);
    }

    private Boolean isBeforeThanPlayEndTime(final GameEntity gameEntity) {
        return DateTimeUtil.isAfterThanNow(gameEntity.getPlayEndDatetime());
    }

    private Boolean isAfterReviewPossibleTime(final GameEntity gameEntity) {
        final LocalDateTime reviewDeadlineDatetime = gameEntity.getPlayEndDatetime().plusDays(REVIEW_POSSIBLE_DAYS);

        return DateTimeUtil.isEqualOrAfter(reviewDeadlineDatetime, LocalDateTime.now());
    }

    private Member getReviewedMember(final GameEntity gameEntity, final Long reviewedMemberId) {
        return getConfirmedMembers(gameEntity)
                .stream()
                .filter(confirmedMember -> confirmedMember.getId() == reviewedMemberId)
                .findFirst()
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, reviewedMemberId));
    }

    private List<Member> getConfirmedMembers(GameEntity gameEntity) {
        return gameMemberRepository.findAllByGameIdAndStatus(gameEntity.getId(), CONFIRMED)
                .stream()
                .map(gameMember -> memberRepository.getMemberById(gameMember.getMemberId()))
                .toList();
    }
}
