package kr.pickple.back.alarm.handler;

import kr.pickple.back.alarm.event.game.GameAlarmEvent;
import kr.pickple.back.alarm.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alarm.event.game.GameMemberRejectedEvent;
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
    public void sendAlarmToGameHost(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
        gameAlarmService.createGameJoinAlarm(gameJoinRequestNotificationEvent);
    }

    @Async
    @EventListener
    public void sendAlarmToGameMemberOnJoin(final GameMemberJoinedEvent gameMemberJoinedEvent) {
        gameAlarmService.createGuestApproveAlarm(gameMemberJoinedEvent);
    }

    @Async
    @EventListener
    public void sendAlarmToGameMemberOnRejection(final GameMemberRejectedEvent gameMemberRejectedEvent) {
        gameAlarmService.createGuestDeniedAlarm(gameMemberRejectedEvent);
    }
}
