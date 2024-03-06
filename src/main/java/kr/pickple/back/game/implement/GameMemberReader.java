package kr.pickple.back.game.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameMemberReader {

    private final GameMemberRepository gameMemberRepository;
    private final MemberReader memberReader;
    private final GameReader gameReader;

    public GameMember readGameMemberByMemberIdAndGameId(Long loggedInMemberId, Long gameId) {
        final GameMemberEntity gameMemberEntity = gameMemberRepository.findByMemberIdAndGameIdAndStatus(
                        loggedInMemberId,
                        gameId,
                        CONFIRMED
                )
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, loggedInMemberId));

        final MemberDomain memberDomain = memberReader.readByMemberId(gameMemberEntity.getMemberId());
        final GameDomain gameDomain = gameReader.read(gameMemberEntity.getGameId());

        return GameMemberMapper.mapGameMemberEntityToDomain(gameMemberEntity, memberDomain, gameDomain);
    }
}
