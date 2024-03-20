package kr.pickple.back.game.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class GameMemberWriter {

    private final GameMemberReader gameMemberReader;
    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;

    public GameMember register(final Member member, final Game game) {
        final Long memberId = member.getMemberId();
        final Long gameId = game.getGameId();

        if (gameMemberRepository.existsByGameIdAndMemberId(gameId, memberId)) {
            throw new GameException(GAME_MEMBER_IS_EXISTED, gameId, memberId);
        }

        final GameMember gameMember = GameMember.builder()
                .status(WAITING)
                .member(member)
                .game(game)
                .build();

        final GameMemberEntity gameMemberEntity = GameMemberMapper.mapGameMemberDomainToEntity(gameMember);
        final GameMemberEntity savedGameMemberEntity = gameMemberRepository.save(gameMemberEntity);
        gameMember.updateGameMemberId(savedGameMemberEntity.getId());

        return gameMember;
    }

    public void updateMemberRegistrationStatus(final GameMember gameMember, final RegistrationStatus status) {
        gameMember.updateRegistrationStatus(status);
        gameMemberRepository.updateRegistrationStatus(gameMember.getGameMemberId(), status);

        if (gameMember.isStatusChangedFromWaitingToConfirmed(status)) {
            final Game game = gameMember.getGame();
            game.increaseMemberCount();
            gameRepository.updateMemberCountAndStatus(game.getGameId(), game.getMemberCount(), game.getStatus());
        }
    }

    public void updateReviewDone(final Long memberId, final Long gameId) {
        final GameMember gameMember = gameMemberReader.readConfirmedStatusByMemberIdAndGameId(memberId, gameId);

        gameMemberRepository.updateReviewDone(gameMember.getGameMemberId(), true);
    }

    public void deleteGameMember(final GameMember gameMember) {
        gameMemberRepository.deleteById(gameMember.getGameMemberId());
    }
}
