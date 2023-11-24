package kr.pickple.back.alarm.handler;

import kr.pickple.back.alarm.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alarm.service.CrewAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrewAlarmEventHandler {

    private final CrewAlarmService crewAlarmService;

    @Async
    @EventListener
    public void sendAlarmToCrewLeader(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
        crewAlarmService.createCrewJoinAlarm(crewJoinRequestNotificationEvent);
    }

    @Async
    @EventListener
    public void sendAlarmToCrewMemberOnJoin(final CrewMemberJoinedEvent crewMemberJoinedEvent) {
        crewAlarmService.createCrewMemberApproveAlarm(crewMemberJoinedEvent);
    }

    @Async
    @EventListener
    public void sendAlarmToCrewMemberOnRejection(final CrewMemberRejectedEvent crewMemberRejectedEvent) {
        crewAlarmService.createCrewMemberDeniedAlarm(crewMemberRejectedEvent);
    }
}
