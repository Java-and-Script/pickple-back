package kr.pickple.back.alarm.event.crew;

import kr.pickple.back.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CrewJoinRequestNotificationEvent {

    private final Long crewId;
    private final Member memberId;
}
