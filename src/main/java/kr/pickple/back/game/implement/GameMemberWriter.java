package kr.pickple.back.game.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameMemberDomain;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.domain.MemberDomain;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class GameMemberWriter {

    private final GameMemberRepository gameMemberRepository;
    private final GameRepository gameRepository;
    private final GameMemberReader gameMemberReader;

    public GameMemberDomain register(final MemberDomain member, final GameDomain game) {
        final Long memberId = member.getMemberId();
        final Long gameId = game.getGameId();

        if (gameMemberRepository.existsByGameIdAndMemberId(gameId, memberId)) {
            throw new GameException(GAME_MEMBER_IS_EXISTED, gameId, memberId);
        }

        final GameMemberDomain gameMemberDomain = GameMemberDomain.builder()
                .status(WAITING)
                .member(member)
                .game(game)
                .build();

        final GameMemberEntity gameMemberEntity = GameMemberMapper.mapGameMemberDomainToEntity(gameMemberDomain);
        final GameMemberEntity savedGameMemberEntity = gameMemberRepository.save(gameMemberEntity);
        gameMemberDomain.updateGameMemberId(savedGameMemberEntity.getId());

        return gameMemberDomain;
    }

    public void updateMemberRegistrationStatus(final GameMemberDomain gameMemberDomain, final RegistrationStatus status) {
        gameMemberDomain.updateRegistrationStatus(status);
        gameMemberRepository.updateRegistrationStatus(gameMemberDomain.getGameMemberId(), status);

        if (gameMemberDomain.isStatusChangedFromWaitingToConfirmed(status)) {
            final GameDomain game = gameMemberDomain.getGame();
            game.increaseMemberCount();
            gameRepository.updateMemberCountAndStatus(game.getGameId(), game.getMemberCount(), game.getStatus());
        }
    }

    public void updateReviewDone(Long loggedInMemberId, Long gameId) {
        GameMemberDomain gameMemberDomain = gameMemberReader.readGameMemberByMemberIdAndGameId(loggedInMemberId, gameId);

        gameMemberRepository.updateReviewDone(gameMemberDomain.getGameMemberId(), true);
    }

    public void deleteGameMember(GameMemberDomain gameMemberDomain) {
        gameMemberRepository.deleteByGameIdAndMemberId(gameMemberDomain.getGame().getGameId(), gameMemberDomain.getMember().getMemberId());
    }
}
