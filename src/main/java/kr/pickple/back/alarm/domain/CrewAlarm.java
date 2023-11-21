package kr.pickple.back.alarm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alarm.util.AlarmStatusConverter;
import kr.pickple.back.alarm.util.AlarmTypeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 10)
    @Convert(converter = AlarmStatusConverter.class)
    private AlarmStatus isRead = FALSE;

    @NotNull
    @Column(length = 20)
    @Convert(converter = AlarmTypeConverter.class)
    private AlarmType alarmType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private CrewAlarm(
            final AlarmType alarmType,
            final Crew crew,
            final Member member
    ) {
        this.alarmType = alarmType;
        this.createdAt = super.getCreatedAt();
        this.crew = crew;
        this.member = member;
    }

    public void updateStatus(AlarmStatus status) {
        this.isRead = status;
    }
}
