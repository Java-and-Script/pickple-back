package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.dto.request.GameAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.event.game.GameAlarmEvent;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.repository.GameAlarmRepository;
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
    private final SseEmitterService sseEmitterService;

    @Transactional
    public GameAlarmResponse createGameJoinAlarm(final GameAlarmEvent gameAlarmEvent) {

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

        sseEmitterService.notify(host.getId(), response);
        return response;
    }

    @Transactional
    public GameAlarmResponse createGuestApproveAlarm(final GameAlarmEvent gameAlarmEvent) {

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

        sseEmitterService.notify(member.getId(), response);
        return response;
    }

    @Transactional
    public GameAlarmResponse createGuestDeniedAlarm(final GameAlarmEvent gameAlarmEvent) {

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

        sseEmitterService.notify(member.getId(), response);
        return response;
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
        try {
            sseEmitterService.notify(member.getId(), gameAlarm);
        } catch (Exception e) {
            log.info("해당 회원에게 알람 전송 중 오류가 발생되었습니다. : " + member.getId(), e);
        }
    }

    private void sendAlarmToGameHost(final Member gameHost, final GameAlarmResponse gameAlarm) {
        sendAlarmToMember(gameHost, gameAlarm);
    }

    private void sendAlarmToGameApplyMembers(List<Member> members, GameAlarmResponse gameAlarm) {

        if (members == null) {
            log.debug("해당 경기에 참여 신청을 한 회원을 찾지 못하였습니다.");
            return;
        }
        members.forEach(member -> sendAlarmToMember(member, gameAlarm));
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
