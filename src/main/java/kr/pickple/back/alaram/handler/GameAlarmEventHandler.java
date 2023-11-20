package kr.pickple.back.alaram.handler;

import kr.pickple.back.alaram.domain.GameAlarm;
import kr.pickple.back.alaram.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alaram.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alaram.event.game.GameMemberRejectedEvent;
import kr.pickple.back.alaram.service.GameAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameAlarmEventHandler {

    private final GameAlarmService gameAlarmService;

    @Async
    @EventListener
    public void sendAlarmToGameHost(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
        final GameAlarm gameAlarm = gameAlarmService.createGameJoinAlaram(gameJoinRequestNotificationEvent); //알람 생성
        gameAlarmService.emitMessage(gameAlarm); //SSE로 알람 메시지 전송 - sseServcie로 변경?
    }

    @Async
    @EventListener
    public void sendAlarmToGameMemberOnJoin(final GameMemberJoinedEvent gameMemberJoinedEvent) {
        final GameAlarm gameAlarm = gameAlarmService.createGuestApproveAlaram(gameMemberJoinedEvent); //알람 생성
        gameAlarmService.emitMessage(gameAlarm); //SSE로 알람 메시지 전송
    }

    @Async
    @EventListener
    public void sendAlarmToGameMemberOnRejection(final GameMemberRejectedEvent gameMemberRejectedEvent) {
        final GameAlarm gameAlarm = gameAlarmService.createGuestDeniedAlaram(gameMemberRejectedEvent); //알람 생성
        gameAlarmService.emitMessage(gameAlarm); //SSE로 알람 메시지 전송
    }
}
