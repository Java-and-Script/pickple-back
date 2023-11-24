package kr.pickple.back.alarm.handler;

import kr.pickple.back.alarm.event.crew.CrewAlarmEvent;
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
    public void sendAlarmToCrewLeader(final CrewAlarmEvent crewAlarmEvent) {
        crewAlarmService.createCrewJoinAlarm(crewAlarmEvent);
    }

    @Async
    @EventListener
    public void sendAlarmToCrewMemberOnJoin(final CrewAlarmEvent crewAlarmEvent) {
        crewAlarmService.createCrewMemberApproveAlarm(crewAlarmEvent);
    }

    @Async
    @EventListener
    public void sendAlarmToCrewMemberOnRejection(final CrewAlarmEvent crewAlarmEvent) {
        crewAlarmService.createCrewMemberDeniedAlarm(crewAlarmEvent);
    }
}
