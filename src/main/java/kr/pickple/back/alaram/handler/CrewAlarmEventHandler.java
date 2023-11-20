package kr.pickple.back.alaram.handler;

import kr.pickple.back.alaram.dto.response.CrewAlarmResponse;
import kr.pickple.back.alaram.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alaram.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alaram.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alaram.service.CrewAlarmService;
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
        final CrewAlarmResponse crewAlarm = crewAlarmService.createCrewJoinAlaram(crewJoinRequestNotificationEvent); // 알람 생성
        crewAlarmService.emitMessage(crewAlarm); // SSE로 알람 메시지 전송
    }

    @Async
    @EventListener
    public void sendAlarmToCrewMemberOnJoin(final CrewMemberJoinedEvent crewMemberJoinedEvent) {
        final CrewAlarmResponse crewAlarm = crewAlarmService.createCrewMemberApproveAlarm(crewMemberJoinedEvent); // 알람 생성
        crewAlarmService.emitMessage(crewAlarm); // SSE로 알람 메시지 전송
    }

    @Async
    @EventListener
    public void sendAlarmToCrewMemberOnRejection(final CrewMemberRejectedEvent crewMemberRejectedEvent) {
        final CrewAlarmResponse crewAlarm = crewAlarmService.createCrewMemberDeniedAlarm(crewMemberRejectedEvent); // 알람 생성
        crewAlarmService.emitMessage(crewAlarm); // SSE로 알람 메시지 전송
    }
}
