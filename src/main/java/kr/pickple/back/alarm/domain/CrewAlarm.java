package kr.pickple.back.alarm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alarm.util.CrewAlarmTypeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Boolean isRead = false;

    @NotNull
    @Column(length = 30)
    @Convert(converter = CrewAlarmTypeConverter.class)
    private CrewAlarmType crewAlarmType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private CrewAlarm(
            final CrewAlarmType crewAlarmType,
            final Crew crew,
            final Member member
    ) {
        this.crewAlarmType = crewAlarmType;
        this.crew = crew;
        this.member = member;
    }

    public void updateStatus(final Boolean status) {
        this.isRead = status;
    }
}
