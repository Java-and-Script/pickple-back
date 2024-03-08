package kr.pickple.back.game.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.alarm.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alarm.event.game.GameMemberRejectedEvent;
import kr.pickple.back.chat.implement.ChatReader;
import kr.pickple.back.chat.implement.ChatWriter;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.dto.mapper.GameResponseMapper;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.implement.GameMemberReader;
import kr.pickple.back.game.implement.GameMemberWriter;
import kr.pickple.back.game.implement.GameReader;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameMemberService {

    private final MemberReader memberReader;
    private final GameReader gameReader;
    private final GameMemberReader gameMemberReader;
    private final GameMemberWriter gameMemberWriter;
    private final ChatReader chatReader;
    private final ChatWriter chatWriter;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void registerGameMember(final Long gameId, final Long loggedInMemberId) {
        final Game gameDomain = gameReader.read(gameId);
        final Member memberDomain = memberReader.readByMemberId(loggedInMemberId);

        gameMemberWriter.register(memberDomain, gameDomain);

        eventPublisher.publishEvent(GameJoinRequestNotificationEvent.builder()
                .gameId(gameId)
                .memberId(loggedInMemberId)
                .build());
    }

    public GameResponse findAllGameMembers(
            final Long loggedInMemberId,
            final Long gameId,
            final RegistrationStatus status
    ) {
        final GameMember gameMember = gameMemberReader.readByMemberIdAndGameId(loggedInMemberId, gameId);
        final Game game = gameMember.getGame();
        final Member member = gameMember.getMember();

        if (!game.isHost(member.getMemberId()) && status == WAITING) {
            throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
        }

        final List<Member> members = gameMemberReader.readMembersByGameIdAndStatus(gameId, status);

        return GameResponseMapper.mapToGameResponseDto(game, members);
    }

    @Transactional
    public void updateGameMemberRegistrationStatus(
            final Long loggedInMemberId,
            final Long gameId,
            final Long memberId,
            final RegistrationStatus newRegistrationStatus
    ) {
        final GameMember gameMember = gameMemberReader.readByMemberIdAndGameId(loggedInMemberId, gameId);
        final Game game = gameMember.getGame();

        if (!game.isHost(loggedInMemberId)) {
            throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
        }

        gameMemberWriter.updateMemberRegistrationStatus(gameMember, newRegistrationStatus);
        chatWriter.enterRoom(gameMember.getMember(), chatReader.readRoomByGameId(gameId));

        eventPublisher.publishEvent(GameMemberJoinedEvent.builder()
                .gameId(gameId)
                .memberId(memberId)
                .build());
    }

    @Transactional
    public void deleteGameMember(final Long loggedInMemberId, final Long gameId, final Long memberId) {
        final GameMember gameMember = gameMemberReader.readByMemberIdAndGameId(memberId, gameId);
        final Game game = gameMember.getGame();

        if (game.isHost(loggedInMemberId)) {
            validateIsHostSelfDeleted(loggedInMemberId, memberId);

            gameMemberWriter.deleteGameMember(gameMember);

            eventPublisher.publishEvent(GameMemberRejectedEvent.builder()
                    .gameId(gameId)
                    .memberId(memberId)
                    .build());

            return;
        }

        if (loggedInMemberId.equals(memberId)) {
            cancelGameMember(gameMember);

            return;
        }

        throw new GameException(GAME_NOT_ALLOWED_TO_DELETE_GAME_MEMBER, loggedInMemberId);
    }

    private void validateIsHostSelfDeleted(final Long loggedInMemberId, final Long memberId) {
        if (loggedInMemberId.equals(memberId)) {
            throw new GameException(GAME_HOST_CANNOT_BE_DELETED, loggedInMemberId);
        }
    }

    private void cancelGameMember(final GameMember gameMember) {
        final RegistrationStatus status = gameMember.getStatus();

        if (status != WAITING) {
            throw new GameException(GAME_MEMBER_STATUS_IS_NOT_WAITING, status);
        }

        gameMemberWriter.deleteGameMember(gameMember);
    }
}
