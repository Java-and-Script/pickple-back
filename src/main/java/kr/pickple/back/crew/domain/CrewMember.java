package kr.pickple.back.crew.domain;

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
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.common.util.RegistrationStatusAttributeConverter;
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
    @Convert(converter = RegistrationStatusAttributeConverter.class)
    @Column(length = 10)
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
    private CrewMember(final Member member, final Crew crew) {
        this.member = member;
        this.crew = crew;
    }

    public Boolean equalsStatus(final RegistrationStatus status) {
        return this.status == status;
    }

    public void confirmRegistration() {
        this.status = RegistrationStatus.CONFIRMED;
    }

    public void updateStatus(final RegistrationStatus registrationStatus) {
        this.status = registrationStatus;
    }
}
