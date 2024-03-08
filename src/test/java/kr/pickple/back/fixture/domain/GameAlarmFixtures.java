package kr.pickple.back.fixture.domain;

import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.member.repository.entity.MemberEntity;

import static kr.pickple.back.alarm.domain.GameAlarmType.HOST_WAITING;

public class GameAlarmFixtures {

    public static GameAlarm gameAlarmBuild(final MemberEntity member, final GameEntity gameEntity) {
        return GameAlarm.builder()
                .game(gameEntity)
                .member(member)
                .gameAlarmType(HOST_WAITING)
                .build();
    }
}
