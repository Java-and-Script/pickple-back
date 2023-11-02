package kr.pickple.back.crew.domain;

<<<<<<< HEAD
import jakarta.persistence.*;
=======
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
>>>>>>> 4f5ee62 (feat: 크루 가입 신청 상태, 게스트 모집 참여 신청 상태에 해당하는 RegistrationStatus에 대한 컨버터 설정 추가)
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.common.util.RegistrationStatusConverter;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.pickple.back.common.domain.RegistrationStatus.WAITING;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
<<<<<<< HEAD
=======
    @Convert(converter = RegistrationStatusConverter.class)
>>>>>>> 4f5ee62 (feat: 크루 가입 신청 상태, 게스트 모집 참여 신청 상태에 해당하는 RegistrationStatus에 대한 컨버터 설정 추가)
    @Column(length = 10)
    @Convert(converter = RegistrationStatusConverter.class)
    private RegistrationStatus status = WAITING;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @Builder
    private CrewMember(
            final Long id,
            final RegistrationStatus status,
            final Member member,
            final Crew crew
    ) {
        this.member = member;
        this.crew = crew;
    }
}
