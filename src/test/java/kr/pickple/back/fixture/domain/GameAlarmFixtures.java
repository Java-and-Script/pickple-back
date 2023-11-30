package kr.pickple.back.fixture.domain;

import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;

import static kr.pickple.back.alarm.domain.GameAlarmType.HOST_WAITING;

public class GameAlarmFixtures {

    public static GameAlarm gameAlarmBuild(final Member member, final Game game) {
        return GameAlarm.builder()
                .game(game)
                .member(member)
                .gameAlarmType(HOST_WAITING)
                .build();
    }
}
