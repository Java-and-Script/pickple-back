package kr.pickple.back.alarm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alarm.util.AlarmExistsStatusConverter;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.pickple.back.alarm.domain.AlarmExistsStatus.NOT_EXISTS;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 10)
    @Convert(converter = AlarmExistsStatusConverter.class)
    private AlarmExistsStatus alarmExistsStatus = NOT_EXISTS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_alarm_id")
    private CrewAlarm crewAlarm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_alarm_id")
    private GameAlarm gameAlarm;

    @Builder
    private Alarm(
            final Member member,
            final CrewAlarm crewAlarm,
            final GameAlarm gameAlarm
    ) {
        this.member = member;
        this.crewAlarm = crewAlarm;
        this.gameAlarm = gameAlarm;
    }
}
