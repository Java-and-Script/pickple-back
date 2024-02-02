package kr.pickple.back.alarm.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.pickple.back.alarm.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alarm.service.CrewAlarmService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrewAlarmEventHandler {

    private final CrewAlarmService crewAlarmService;

    @Async
    @TransactionalEventListener
    public void sendAlarmToCrewLeader(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
        crewAlarmService.createCrewJoinAlarm(crewJoinRequestNotificationEvent);
    }

    @Async
    @TransactionalEventListener
    public void sendAlarmToCrewMemberOnJoin(final CrewMemberJoinedEvent crewMemberJoinedEvent) {
        crewAlarmService.createCrewMemberApproveAlarm(crewMemberJoinedEvent);
    }

    @Async
    @TransactionalEventListener
    public void sendAlarmToCrewMemberOnRejection(final CrewMemberRejectedEvent crewMemberRejectedEvent) {
        crewAlarmService.createCrewMemberDeniedAlarm(crewMemberRejectedEvent);
    }
}
