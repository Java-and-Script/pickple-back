package kr.pickple.back.alarm.event.crew;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrewJoinRequestNotificationEvent {

    private final Long crewId;

    public Long getCrewId(){
        return crewId;
    }
}
