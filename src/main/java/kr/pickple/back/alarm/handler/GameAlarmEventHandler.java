package kr.pickple.back.alarm.handler;

import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alarm.event.game.GameMemberRejectedEvent;
import kr.pickple.back.alarm.service.GameAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GameAlarmEventHandler {

    private final GameAlarmService gameAlarmService;

    @Async
    @Transactional
    @EventListener
    public void sendAlarmToGameHost(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
        final GameAlarmResponse gameAlarm = gameAlarmService.createGameJoinAlarm(gameJoinRequestNotificationEvent); //알람 생성
        gameAlarmService.emitMessage(gameAlarm); //SSE로 알람 메시지 전송 - sseServcie로 변경?
    }

    @Async
    @Transactional
    @EventListener
    public void sendAlarmToGameMemberOnJoin(final GameMemberJoinedEvent gameMemberJoinedEvent) {
        final GameAlarmResponse gameAlarm = gameAlarmService.createGuestApproveAlarm(gameMemberJoinedEvent); //알람 생성
        gameAlarmService.emitMessage(gameAlarm); //SSE로 알람 메시지 전송
    }

    @Async
    @Transactional
    @EventListener
    public void sendAlarmToGameMemberOnRejection(final GameMemberRejectedEvent gameMemberRejectedEvent) {
        final GameAlarmResponse gameAlarm = gameAlarmService.createGuestDeniedAlarm(gameMemberRejectedEvent); //알람 생성
        gameAlarmService.emitMessage(gameAlarm); //SSE로 알람 메시지 전송
    }
}
