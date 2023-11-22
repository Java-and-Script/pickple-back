package kr.pickple.back.alarm.handler;

import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alarm.service.CrewAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CrewAlarmEventHandler {

    private final CrewAlarmService crewAlarmService;

    @Async
    @Transactional
    @EventListener
    public void sendAlarmToCrewLeader(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
        final CrewAlarmResponse crewAlarm = crewAlarmService.createCrewJoinAlarm(crewJoinRequestNotificationEvent);
        crewAlarmService.emitMessage(crewAlarm);
    }

    @Async
    @Transactional
    @EventListener
    public void sendAlarmToCrewMemberOnJoin(final CrewMemberJoinedEvent crewMemberJoinedEvent) {
        final CrewAlarmResponse crewAlarm = crewAlarmService.createCrewMemberApproveAlarm(crewMemberJoinedEvent);
        crewAlarmService.emitMessage(crewAlarm);
    }

    @Async
    @Transactional
    @EventListener
    public void sendAlarmToCrewMemberOnRejection(final CrewMemberRejectedEvent crewMemberRejectedEvent) {
        final CrewAlarmResponse crewAlarm = crewAlarmService.createCrewMemberDeniedAlarm(crewMemberRejectedEvent);
        crewAlarmService.emitMessage(crewAlarm);
    }
}
