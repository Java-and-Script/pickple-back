package kr.pickple.back.alaram.service;

import kr.pickple.back.alaram.domain.GameAlarm;
import kr.pickple.back.alaram.dto.response.GameAlaramResponse;
import kr.pickple.back.alaram.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alaram.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alaram.event.game.GameMemberRejectedEvent;
import kr.pickple.back.alaram.repository.GameAlarmRepository;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.pickple.back.alaram.domain.AlarmType.*;
import static kr.pickple.back.game.exception.GameExceptionCode.GAME_IS_NOT_HOST;
import static kr.pickple.back.game.exception.GameExceptionCode.GAME_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

//SSE 연결 로직에서는 @Transcational금지
@Service
@RequiredArgsConstructor
public class GameAlarmService {

    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final GameAlarmRepository gameAlarmRepository;

    public GameAlarm createGameJoinAlaram(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
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
        return GameAlaramResponse.of(gameAlarm).getGameAlarm();
    }

    public GameAlarm createGuestApproveAlaram(final GameMemberJoinedEvent gameMemberJoinedEvent) {

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

        return GameAlaramResponse.of(gameAlarm).getGameAlarm();
    }

    public GameAlarm createGuestDeniedAlaram(final GameMemberRejectedEvent gameMemberRejectedEvent) {

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

        return GameAlaramResponse.of(gameAlarm).getGameAlarm();
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

    //3개의 부분에서 SSE알람으로 존재함 - 구분
    public void emitMessage(GameAlarm gameAlarm) {
        //1. SSE로 알람 생성 - 각 케이스 별 알람 생성

        //2. SSE로 발생된 알람 저장
    }

    //게임 알림 찾기 By ID
    public void findGameAlaramById() {
        //id로 알람 찾기
    }

    //게임 알림 찾기 모두
    public void findGameAlaramAll() {

    }

    //게임 알림 변경 By ID
    public void updateGameAlaramStatus() {

    }

    //게임 알림 삭제
    public void deleteGameAlaram() {

    }
}
