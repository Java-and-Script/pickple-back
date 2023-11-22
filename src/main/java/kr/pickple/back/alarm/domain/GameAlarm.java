package kr.pickple.back.alarm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alarm.util.AlarmStatusConverter;
import kr.pickple.back.alarm.util.GameAlarmTypeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 10)
    @Convert(converter = AlarmStatusConverter.class)
    private AlarmStatus isRead = FALSE;

    @NotNull
    @Column(length = 30)
    @Convert(converter = GameAlarmTypeConverter.class)
    private GameAlarmType gameAlarmType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private GameAlarm(
            final GameAlarmType gameAlarmType,
            final Game game,
            final Member member
    ) {
        this.gameAlarmType = gameAlarmType;
        this.game = game;
        this.member = member;
    }

    public void updateStatus(final AlarmStatus status) {
        this.isRead = status;
    }
}
