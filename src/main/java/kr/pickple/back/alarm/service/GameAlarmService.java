package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.dto.request.GameAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.event.game.GameAlarmEvent;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.repository.GameAlarmRepository;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;
import static kr.pickple.back.alarm.domain.GameAlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_NOT_FOUND;
import static kr.pickple.back.game.exception.GameExceptionCode.GAME_IS_NOT_HOST;
import static kr.pickple.back.game.exception.GameExceptionCode.GAME_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameAlarmService {

    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final GameAlarmRepository gameAlarmRepository;
    private final SseEmitterService sseEmitterService;

    @Transactional
    public void createGameJoinAlarm(final GameAlarmEvent gameAlarmEvent) {

        validateIsHost(gameAlarmEvent);

        final Long gameId = gameAlarmEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Member host = game.getHost();

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
    public void createGuestApproveAlarm(final GameAlarmEvent gameAlarmEvent) {
        final Long gameId = gameAlarmEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Long memberId = gameAlarmEvent.getMemberId();
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
    public void createGuestDeniedAlarm(final GameAlarmEvent gameAlarmEvent) {

        final Long gameId = gameAlarmEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Long memberId = gameAlarmEvent.getMemberId();
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

    private void validateIsHost(final GameAlarmEvent gameAlarmEvent) {
        final Long gameId = gameAlarmEvent.getGameId();
        final Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));

        if (!game.isHost(gameAlarmEvent.getMemberId())) {
            throw new GameException(GAME_IS_NOT_HOST, gameId, game.getHost());
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

    public boolean checkUnreadGameAlarm(final Long memberId) {
        final boolean existsUnreadGameAlarm = gameAlarmRepository.existsByMemberIdAndIsRead(memberId, FALSE);

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
