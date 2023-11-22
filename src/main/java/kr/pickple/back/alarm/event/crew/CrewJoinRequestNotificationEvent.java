package kr.pickple.back.alarm.event.crew;

import kr.pickple.back.member.domain.Member;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrewJoinRequestNotificationEvent {

    private final Long crewId;
    private final Member crewLeader;

    public Long getCrewId() {
        return crewId;
    }

    public Member getCrewLeader() {
        return crewLeader;
    }
}
