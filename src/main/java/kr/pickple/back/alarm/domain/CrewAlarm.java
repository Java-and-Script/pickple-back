package kr.pickple.back.alarm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alarm.util.CrewAlarmTypeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.member.repository.entity.MemberEntity;
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
    private CrewEntity crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Builder
    private CrewAlarm(
            final CrewAlarmType crewAlarmType,
            final CrewEntity crew,
            final MemberEntity member
    ) {
        this.crewAlarmType = crewAlarmType;
        this.crew = crew;
        this.member = member;
    }

    public void updateStatus(final Boolean status) {
        this.isRead = status;
    }
}
