package kr.pickple.back.game.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameMember;
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

    public GameMember register(final MemberDomain member, final GameDomain game) {
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

        final GameDomain game = gameMember.getGame();
        game.increaseMemberCount();
        gameRepository.updateMemberCountAndStatus(game.getGameId(), game.getMemberCount(), game.getStatus());
    }

}
