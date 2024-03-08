package kr.pickple.back.fixture.domain;

import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.member.repository.entity.MemberEntity;

import static kr.pickple.back.alarm.domain.CrewAlarmType.CREW_LEADER_WAITING;

public class CrewAlarmFixtures {

    public static CrewAlarm crewAlarmBuild(final MemberEntity member,final CrewEntity crew) {
        return CrewAlarm.builder()
                .crew(crew)
                .member(member)
                .crewAlarmType(CREW_LEADER_WAITING)
                .build();
    }
}
