package kr.pickple.back.fixture.domain;

import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.member.domain.Member;

import static kr.pickple.back.alarm.domain.CrewAlarmType.CREW_LEADER_WAITING;

public class CrewAlarmFixtures {

    public static CrewAlarm crewAlarmBuild(final Member member,final CrewEntity crew) {
        return CrewAlarm.builder()
                .crew(crew)
                .member(member)
                .crewAlarmType(CREW_LEADER_WAITING)
                .build();
    }
}
