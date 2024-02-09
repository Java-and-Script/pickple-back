package kr.pickple.back.alarm.service;

import static kr.pickple.back.alarm.domain.GameAlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.dto.request.GameAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alarm.event.game.GameMemberRejectedEvent;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.repository.GameAlarmRepository;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameAlarmService {

    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final GameAlarmRepository gameAlarmRepository;
    private final SseEmitterService sseEmitterService;

    @Transactional
    public void createGameJoinAlarm(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {

        validateIsHost(gameJoinRequestNotificationEvent);

        final Long gameId = gameJoinRequestNotificationEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Member host = getMemberInfo(game.getHostId());

        final GameAlarm gameAlarm = GameAlarm.builder()
                .game(game)
                .member(host)
                .gameAlarmType(HOST_WAITING)
                .build();

        gameAlarmRepository.save(gameAlarm);

        final GameAlarmResponse response = GameAlarmResponse.from(gameAlarm);
        sseEmitterService.sendAlarm(host.getId(), response);
    }

    @Transactional
    public void createGuestApproveAlarm(final GameMemberJoinedEvent gameMemberJoinedEvent) {
        final Long gameId = gameMemberJoinedEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Long memberId = gameMemberJoinedEvent.getMemberId();
        final Member member = getMemberInfo(memberId);

        final GameAlarm gameAlarm = GameAlarm.builder()
                .game(game)
                .member(member)
                .gameAlarmType(GUEST_ACCEPT)
                .build();

        gameAlarmRepository.save(gameAlarm);

        final GameAlarmResponse response = GameAlarmResponse.from(gameAlarm);
        sseEmitterService.sendAlarm(member.getId(), response);
    }

    @Transactional
    public void createGuestDeniedAlarm(final GameMemberRejectedEvent gameMemberRejectedEvent) {

        final Long gameId = gameMemberRejectedEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Long memberId = gameMemberRejectedEvent.getMemberId();
        final Member member = getMemberInfo(memberId);

        final GameAlarm gameAlarm = GameAlarm.builder()
                .game(game)
                .member(member)
                .gameAlarmType(GUEST_DENIED)
                .build();

        gameAlarmRepository.save(gameAlarm);

        final GameAlarmResponse response = GameAlarmResponse.from(gameAlarm);
        sseEmitterService.sendAlarm(member.getId(), response);
    }

    private void validateIsHost(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
        final Long gameId = gameJoinRequestNotificationEvent.getGameId();
        final Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));

        if (!game.isHost(gameJoinRequestNotificationEvent.getMemberId())) {
            throw new GameException(GAME_MEMBER_IS_NOT_HOST, gameId, game.getHostId());
        }
    }

    private Game getGameInfo(final Long gameId) {
        final Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
        return game;
    }

    private Member getMemberInfo(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    public List<GameAlarmResponse> findByMemberId(
            final Long loggedInMemberId,
            final Optional<Long> optionalCursorId,
            final Integer size
    ) {
        final List<GameAlarm> gameAlarms = optionalCursorId
                .map(cursorId -> gameAlarmRepository.findByMemberIdAndIdLessThanOrderByCreatedAtDesc(
                        loggedInMemberId,
                        cursorId,
                        PageRequest.of(0, size)
                ))
                .orElseGet(() -> gameAlarmRepository.findByMemberIdOrderByCreatedAtDesc(
                        loggedInMemberId,
                        PageRequest.of(0, size)
                ));

        return gameAlarms.stream()
                .map(GameAlarmResponse::from)
                .toList();
    }

    public boolean checkUnreadGameAlarm(final Long memberId) {
        final boolean existsUnreadGameAlarm = gameAlarmRepository.existsByMemberIdAndIsRead(memberId, false);

        return existsUnreadGameAlarm;
    }

    @Transactional
    public void deleteAllGameAlarms(final Long memberId) {
        gameAlarmRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public void updateGameAlarmById(
            final Long loggedInMemberId,
            final Long gameAlarmId,
            final GameAlarmUpdateStatusRequest gameAlarmUpdateStatusRequest
    ) {
        final Member member = findMemberById(loggedInMemberId);
        final GameAlarm gameAlarm = checkExistGameAlarm(loggedInMemberId, gameAlarmId);

        gameAlarm.updateStatus(gameAlarmUpdateStatusRequest.getIsRead());
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    private GameAlarm checkExistGameAlarm(final Long memberId, final Long gameAlarmId) {
        return gameAlarmRepository.findByMemberIdAndId(memberId, gameAlarmId)
                .orElseThrow(() -> new AlarmException(ALARM_NOT_FOUND, memberId, gameAlarmId));
    }
}
