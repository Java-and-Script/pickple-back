package kr.pickple.back.alaram.handler;

import kr.pickple.back.alaram.domain.CrewAlaram;
import kr.pickple.back.alaram.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alaram.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alaram.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alaram.service.CrewAlaramService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrewAlaramEventHandler {

    private final CrewAlaramService crewAlaramService;

    @Async
    @EventListener
    public void sendAlaramToCrewLeader(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
        final CrewAlaram crewAlaram = crewAlaramService.createAlaram(crewJoinRequestNotificationEvent); // 알람 생성
        crewAlaramService.emitMessage(crewAlaram); // SSE로 알람 메시지 전송
    }

    @Async
    @EventListener
    public void sendAlaramToCrewMemberOnJoin(final CrewMemberJoinedEvent crewMemberJoinedEvent) {
        final CrewAlaram crewAlaram = crewAlaramService.createAlaram(crewMemberJoinedEvent); // 알람 생성
        crewAlaramService.emitMessage(crewAlaram); // SSE로 알람 메시지 전송
    }

    @Async
    @EventListener
    public void sendAlaramToCrewMemberOnRejection(final CrewMemberRejectedEvent crewMemberRejectedEvent) {
        final CrewAlaram crewAlaram = crewAlaramService.createAlaram(crewMemberRejectedEvent); // 알람 생성
        crewAlaramService.emitMessage(crewAlaram); // SSE로 알람 메시지 전송
    }
}
