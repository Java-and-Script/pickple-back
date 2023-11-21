package kr.pickple.back.alarm.event.crew;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrewMemberJoinedEvent {

    private final Long crewId;
    private final Long memberId;

    public Long getCrewId() {
        return crewId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
