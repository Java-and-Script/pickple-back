package kr.pickple.back.alarm.handler;

import kr.pickple.back.alarm.event.game.GameAlarmEvent;
import kr.pickple.back.alarm.service.GameAlarmService;
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
    public void sendAlarmToGameHost(final GameAlarmEvent gameAlarmEvent) {
        gameAlarmService.createGameJoinAlarm(gameAlarmEvent);
    }

    @Async
    @EventListener
    public void sendAlarmToGameMemberOnJoin(final GameAlarmEvent gameAlarmEvent) {
        gameAlarmService.createGuestApproveAlarm(gameAlarmEvent);
    }

    @Async
    @EventListener
    public void sendAlarmToGameMemberOnRejection(final GameAlarmEvent gameAlarmEvent) {
        gameAlarmService.createGuestDeniedAlarm(gameAlarmEvent);
    }
}
