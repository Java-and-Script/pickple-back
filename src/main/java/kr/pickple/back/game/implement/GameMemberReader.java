package kr.pickple.back.game.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameMemberReader {

    private final MemberReader memberReader;
    private final GameReader gameReader;
    private final GameMemberRepository gameMemberRepository;

    public GameMember readConfirmedStatusByMemberIdAndGameId(final Long memberId, final Long gameId) {
        final GameMemberEntity gameMemberEntity = gameMemberRepository.findByMemberIdAndGameIdAndStatus(
                        memberId,
                        gameId,
                        CONFIRMED
                )
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, memberId));

        return getGameMember(gameMemberEntity);
    }

    public GameMember readByMemberIdAndGameId(final Long memberId, final Long gameId) {
        final GameMemberEntity gameMemberEntity = gameMemberRepository.findByMemberIdAndGameId(memberId, gameId)
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, memberId));

        return getGameMember(gameMemberEntity);
    }

    private GameMember getGameMember(final GameMemberEntity gameMemberEntity) {
        final Member member = memberReader.readByMemberId(gameMemberEntity.getMemberId());
        final Game game = gameReader.read(gameMemberEntity.getGameId());

        return GameMemberMapper.mapGameMemberEntityToDomain(gameMemberEntity, member, game);
    }

    public List<GameMember> readAllByMemberIdAndStatus(
            final Long memberId,
            final RegistrationStatus status
    ) {
        return gameMemberRepository.findAllByMemberIdAndStatus(memberId, status)
                .stream()
                .map(gameMemberEntity -> GameMemberMapper.mapGameMemberEntityToDomain(
                                gameMemberEntity,
                                memberReader.readByMemberId(gameMemberEntity.getMemberId()),
                                gameReader.read(gameMemberEntity.getGameId())
                        )
                ).toList();
    }

    public List<Member> readMembersByGameIdAndStatus(final Long gameId, final RegistrationStatus status) {
        return gameMemberRepository.findAllByGameIdAndStatus(gameId, status)
                .stream()
                .map(gameMemberEntity -> memberReader.readByMemberId(gameMemberEntity.getMemberId()))
                .toList();
    }

    public Boolean isReviewDoneByGameIdAndMemberId(final Long gameId, final Long memberId) {
        final GameMemberEntity gameMemberEntity = gameMemberRepository.findByMemberIdAndGameId(memberId, gameId)
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, memberId));

        return gameMemberEntity.isReviewDone();
    }
}
