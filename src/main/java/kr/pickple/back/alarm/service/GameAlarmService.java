package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.dto.request.GameAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alarm.event.game.GameMemberRejectedEvent;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.repository.GameAlarmRepository;
import kr.pickple.back.alarm.util.SseEmitters;
import kr.pickple.back.common.domain.RegistrationStatus;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;
import static kr.pickple.back.alarm.domain.GameAlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_NOT_FOUND;
import static kr.pickple.back.common.domain.RegistrationStatus.WAITING;
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
    private final SseEmitters sseEmitters;

    @Transactional
    public GameAlarmResponse createGameJoinAlarm(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {

        validateIsHost(gameJoinRequestNotificationEvent);

        final Long gameId = gameJoinRequestNotificationEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Member host = game.getHost();

        final GameAlarm gameAlarm = GameAlarm.builder()
                .game(game)
                .member(host)
                .gameAlarmType(HOST_WAITING)
                .build();

        gameAlarmRepository.save(gameAlarm);


        final GameAlarmResponse response = GameAlarmResponse.from(gameAlarm);

        sseEmitters.notify(host.getId(), response);
        return response;
    }

    @Transactional
    public GameAlarmResponse createGuestApproveAlarm(final GameMemberJoinedEvent gameMemberJoinedEvent) {

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

        sseEmitters.notify(member.getId(), response);
        return response;
    }

    @Transactional
    public GameAlarmResponse createGuestDeniedAlarm(final GameMemberRejectedEvent gameMemberRejectedEvent) {

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

        sseEmitters.notify(member.getId(), response);
        return response;
    }


    private void validateIsHost(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
        final Long gameId = gameJoinRequestNotificationEvent.getGameId();
        final Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));

        if (!game.isHost(gameJoinRequestNotificationEvent.getMemberId())) {
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

    private Member getHostOfGame(final Long gameId) {
        final Game game = getGameInfo(gameId);
        return game.getHost();
    }

    private List<Member> getGameMembers(final Long gameId, final RegistrationStatus status) {
        final Game game = getGameInfo(gameId);
        return game.getMembersByStatus(status);
    }

    public void emitMessage(final GameAlarmResponse gameAlarm) {
        final Long gameId = gameAlarm.getGameId();
        final Member gameHost = getHostOfGame(gameId);
        final List<Member> gameApplyMembers = getGameMembers(gameId, WAITING);

        sendAlarmToGameHost(gameHost, gameAlarm);
        sendAlarmToGameApplyMembers(gameApplyMembers, gameAlarm);
    }

    private void sendAlarmToMember(final Member member, final GameAlarmResponse gameAlarm) {
        final SseEmitter gameHostEmitter = sseEmitters.get(member.getId());
        if (gameHostEmitter != null) {
            try {
                gameHostEmitter.send(gameAlarm);
            } catch (IOException e) {
                sseEmitters.remove(member.getId());
                log.info("해당 회원에게 알람 전송 중 오류가 발생되었습니다. : " + member.getId(), e);
            }
        }
    }

    private void sendAlarmToGameHost(final Member gameHost, final GameAlarmResponse gameAlarm) {
        sendAlarmToMember(gameHost, gameAlarm);
    }

    private void sendAlarmToGameApplyMembers(List<Member> members, GameAlarmResponse gameAlarm) {
        for (final Member member : members) {
            sendAlarmToMember(member, gameAlarm);
        }
    }

    public boolean checkUnreadGameAlarm(final Long memberId) {
        final boolean existsUnreadGameAlarm = gameAlarmRepository.existsByMemberIdAndIsRead(memberId, FALSE);
        return existsUnreadGameAlarm;
    }

    public void deleteAllGameAlarms(final Long memberId) {
        gameAlarmRepository.deleteByMemberId(memberId);
    }

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
