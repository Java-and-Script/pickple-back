package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.GameAlarm;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;
import static kr.pickple.back.alarm.domain.AlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_NOT_FOUND;
import static kr.pickple.back.common.domain.RegistrationStatus.WAITING;
import static kr.pickple.back.game.exception.GameExceptionCode.GAME_IS_NOT_HOST;
import static kr.pickple.back.game.exception.GameExceptionCode.GAME_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

//SSE 연결 로직에서는 @Transcational금지
@Slf4j
@Service
@RequiredArgsConstructor
public class GameAlarmService {

    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final GameAlarmRepository gameAlarmRepository;
    //private final AlarmService alarmService;
    private final SseEmitters sseEmitters;

    public GameAlarmResponse createGameJoinAlarm(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
        //1.게임 리포지토리에서 해당 게임의 호스트인지 확인
        validateIsHost(gameJoinRequestNotificationEvent);

        //2. 이벤트로부터 게임 정보 가져오기
        final Long gameId = gameJoinRequestNotificationEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Member host = game.getHost();

        //3. 알람 생성
        final GameAlarm gameAlarm = GameAlarm.builder()
                .game(game)
                .member(host)
                .alarmType(HOST_WAITING)
                .build();

        //4. DB에다가 알림 저장
        gameAlarmRepository.save(gameAlarm);

        final GameAlarmResponse response = GameAlarmResponse.of(gameAlarm);
        //alarmService.notify(host.getId(), response);
        sseEmitters.notify(host.getId(), response);


        return response;
    }

    public GameAlarmResponse createGuestApproveAlarm(final GameMemberJoinedEvent gameMemberJoinedEvent) {

        //1.이벤트로부터 게임 정보, 회원 정보 가져오기
        final Long gameId = gameMemberJoinedEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Long memberId = gameMemberJoinedEvent.getMemberId();
        final Member member = getMemberInfo(memberId);

        //2.알람 생성
        final GameAlarm gameAlarm = GameAlarm.builder()
                .game(game)
                .member(member)
                .alarmType(GUEST_ACCEPT)
                .build();

        //3.알람을 DB에다가 저장
        gameAlarmRepository.save(gameAlarm);

        final GameAlarmResponse response = GameAlarmResponse.of(gameAlarm);
//        alarmService.notify(member.getId(), response);
        sseEmitters.notify(member.getId(), response);


        return response;
    }

    public GameAlarmResponse createGuestDeniedAlarm(final GameMemberRejectedEvent gameMemberRejectedEvent) {

        //1.이벤트로부터 게임 정보, 회원 정보 가져오기
        final Long gameId = gameMemberRejectedEvent.getGameId();
        final Game game = getGameInfo(gameId);
        final Long memberId = gameMemberRejectedEvent.getMemberId();
        final Member member = getMemberInfo(memberId);

        //2.알람 생성
        final GameAlarm gameAlarm = GameAlarm.builder()
                .game(game)
                .member(member)
                .alarmType(GUEST_DENIED)
                .build();

        //3.알람 DB에다가 저장
        gameAlarmRepository.save(gameAlarm);

        final GameAlarmResponse response = GameAlarmResponse.of(gameAlarm);
//        alarmService.notify(member.getId(), response);
        sseEmitters.notify(member.getId(), response);

        return response;
    }


    private void validateIsHost(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
        final Long gameId = gameJoinRequestNotificationEvent.getGameId();
        final Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));

        //해당 게임의 id와 호스트 id를 비교하여, 해당 게임의 호스트가 맞는지 체크
        if (!game.isHost(gameJoinRequestNotificationEvent.getGameId())) {
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

    //SSE 알람을 발송하는 부분
    public void emitMessage(final GameAlarmResponse gameAlarm) {
        final Long gameId = gameAlarm.getGameId();
        final Member gameHost = getHostOfGame(gameId);
        final List<Member> gameApplyMembers = getGameMembers(gameId, WAITING);

        //1. SSE로 알람 생성 - 각 케이스 별 알람 생성(호스트와 게스트에게 메시지 전송)
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

    //게임 호스트에게 가입 신청이 올 시 받는 알람
    private void sendAlarmToGameHost(final Member gameHost, final GameAlarmResponse gameAlarm) {
        sendAlarmToMember(gameHost, gameAlarm);
    }

    //회원(게스트 - 대기)에게 호스트가 승락 시, 상태가 Confirmed로 변하며 승락되었다는 알람
    //회원(게스트 - 대기)에게 호스트가 거절 시, 게임 참여자(Game Memmber) 테이블에서 삭제되며, 거절되었다는 알람
    private void sendAlarmToGameApplyMembers(List<Member> members, GameAlarmResponse gameAlarm) {
        for (final Member member : members) {
            sendAlarmToMember(member, gameAlarm);
        }
    }

//    //게임 알림 찾기 모두 - 미정
//    public void findGameAlarmAll() {
//
//    }

    //게임 알람에서 isRead가 False가 있는지 체크하는 메소드
    public boolean checkUnreadGameAlarm(final Long memberId) {
        //1. 해당 회원의 읽지 않은 알람이 있는지 체크함
        final boolean existsUnreadGameAlarm = gameAlarmRepository.existsByMemberIdAndIsRead(memberId, FALSE);

        //2. 반환
        return existsUnreadGameAlarm;
    }

    //게임 알림 찾기 By ID
    public GameAlarm findGameAlarmById(final Long gameAlarmId) {
        //1.알람 ID로 해당 알림 찾기
        final GameAlarm gameAlarm = checkExistGameAlarm(gameAlarmId);

        //2.찾은 알람 반환
        return gameAlarm;
    }

    //게임 알림 변경 By ID
    public void updateGameAlarmStatus(final Long gameAlarmId, final String isRead) {
        //1. 알람 ID로 해당 알림 찾기
        final GameAlarm gameAlarm = checkExistGameAlarm(gameAlarmId);
        final AlarmStatus alarmStatus = AlarmStatus.from(isRead);

        //2. 상태 업데이트
        gameAlarm.updateStatus(alarmStatus);

        //3. 저장
        gameAlarmRepository.save(gameAlarm);
    }

    private GameAlarm checkExistGameAlarm(final Long gameAlarmId) {
        final GameAlarm gameAlarm = gameAlarmRepository.findById(gameAlarmId)
                .orElseThrow(() -> new AlarmException(ALARM_NOT_FOUND, gameAlarmId));

        return gameAlarm;
    }

    //게임의 모든 알림을 삭제
    public void deleteAllGameAlarms() {
        //1.DB에서 생성된 모든 게임 알람을 삭제함
        gameAlarmRepository.deleteAll();
    }
}
