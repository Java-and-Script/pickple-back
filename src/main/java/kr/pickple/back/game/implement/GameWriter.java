package kr.pickple.back.game.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.address.service.kakao.KakaoAddressSearchClient;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.domain.NewGame;
import kr.pickple.back.game.dto.request.MannerScoreReview;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GamePositionRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class GameWriter {

    private final AddressReader addressReader;
    private final MemberReader memberReader;
    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;
    private final GamePositionRepository gamePositionRepository;
    private final KakaoAddressSearchClient kakaoAddressSearchClient;

    public Game create(final NewGame newGame) {
        final Point point = kakaoAddressSearchClient.fetchAddress(newGame.getMainAddress());

        final MainAddress mainAddress = addressReader.readMainAddressByNames(
                newGame.getAddressDepth1Name(),
                newGame.getAddressDepth2Name()
        );

        final GameEntity gameEntity = GameMapper.mapNewGameDomainToEntity(newGame, point, mainAddress);
        final GameEntity savedGameEntity = gameRepository.save(gameEntity);

        setPositionsToGame(newGame.getPositions(), savedGameEntity.getId());

        return GameMapper.mapGameEntityToDomain(
                savedGameEntity,
                mainAddress,
                newGame.getHost(),
                newGame.getPositions()
        );
    }

    private void setPositionsToGame(final List<Position> positions, final Long gameId) {
        validateIsDuplicatedPositions(positions);

        gamePositionRepository.saveAll(GameMapper.mapToGamePositionEntities(positions, gameId));
    }

    private void validateIsDuplicatedPositions(final List<Position> positions) {
        final long distinctPositionsSize = positions.stream()
                .distinct()
                .count();

        if (distinctPositionsSize != positions.size()) {
            throw new GameException(GAME_POSITIONS_IS_DUPLICATED, positions);
        }
    }

    public void updateMemberRegistrationStatus(final GameStatus status, final Long gameId) {
        gameRepository.updateRegistrationStatus(status, gameId);
    }

    public void reviewMannerScores(
            final Long memberId,
            final Long gameId,
            final List<MannerScoreReview> mannerScoreReviews
    ) {
        final Member member = memberReader.readByMemberId(memberId);

        mannerScoreReviews.forEach(review -> {
            final Member reviewedMember = getReviewedMember(gameId, review.getMemberId());

            if (member.equals(reviewedMember)) {
                throw new GameException(GAME_MEMBER_CANNOT_REVIEW_SELF, memberId, reviewedMember.getMemberId());
            }

            reviewedMember.updateMannerScore(review.getMannerScore());
            memberRepository.updateMannerScore(
                    reviewedMember.getMemberId(),
                    reviewedMember.getMannerScore(),
                    reviewedMember.getMannerScoreCount()
            );
        });

    }

    private Member getReviewedMember(final Long gameId, final Long reviewedMemberId) {
        return getConfirmedMembers(gameId)
                .stream()
                .filter(confirmedMember -> confirmedMember.isIdMatched(reviewedMemberId))
                .findFirst()
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, reviewedMemberId));
    }

    private List<Member> getConfirmedMembers(final Long gameId) {
        return gameMemberRepository.findAllByGameIdAndStatus(gameId, CONFIRMED)
                .stream()
                .map(gameMember -> memberReader.readByMemberId(gameMember.getMemberId()))
                .toList();
    }
}
