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
import kr.pickple.back.game.dto.response.GameMemberRegistrationStatusResponse;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.dto.response.MemberGameResponse;
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

    /**
     * 게스트 모집 참여 신청
     */
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

    /**
     * 게스트 모집에 참여 신청된 혹은 확정된 사용자 정보 목록 조회
     */
    public GameResponse findAllGameMembersByStatus(
            final Long gameId,
            final RegistrationStatus status
    ) {
        final Game game = gameReader.read(gameId);

        final List<Member> members = gameMemberReader.readMembersByGameIdAndStatus(gameId, status);

        return GameResponseMapper.mapToGameResponseDto(game, members);
    }

    /**
     * 게스트 모집 참여 신청 수락
     */
    @Transactional
    public void updateGameMemberRegistrationStatus(
            final Long loggedInMemberId,
            final Long gameId,
            final Long memberId,
            final RegistrationStatus newRegistrationStatus
    ) {
        final GameMember gameMember = gameMemberReader.readByMemberIdAndGameId(memberId, gameId);
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

    /**
     * 게스트 모집 참여 신청 거절/취소
     */
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

    /**
     * 사용자의 참여 확정 게스트 모집글 목록 조회
     */
    public List<MemberGameResponse> findAllJoinedGames(final Long memberId, final RegistrationStatus status) {
        return gameMemberReader.readAllByMemberIdAndStatus(memberId, status)
                .stream()
                .map(memberGame -> GameResponseMapper.mapToMemberGameResponseDto(
                                memberGame.getGame(),
                                gameMemberReader.readMembersByGameIdAndStatus(memberGame.getGame().getGameId(), CONFIRMED),
                                memberGame.isReviewDone()
                        )
                ).toList();
    }

    /**
     * 사용자가 만든 게스트 모집글 목록 조회
     */
    public List<MemberGameResponse> findAllCreatedGames(final Long hostId) {
        final List<Game> createdGames = gameReader.readAllByHostId(hostId);

        return createdGames.stream()
                .map(game -> GameResponseMapper.mapToMemberGameResponseDto(
                                game,
                                gameMemberReader.readMembersByGameIdAndStatus(game.getGameId(), CONFIRMED),
                                gameMemberReader.isReviewDoneByGameIdAndMemberId(game.getGameId(), hostId)
                        )
                ).toList();
    }

    /**
     * 사용자의 게스트 모집 신청 여부 조회
     */
    public GameMemberRegistrationStatusResponse findRegistrationStatusForGame(
            final Long memberId,
            final Long gameId
    ) {
        final GameMember gameMember = gameMemberReader.readConfirmedStatusByMemberIdAndGameId(memberId, gameId);

        return GameResponseMapper.mapToGameMemberRegistrationStatusResponseDto(
                gameMember.getStatus(),
                gameMember.isReviewDone()
        );
    }
}
