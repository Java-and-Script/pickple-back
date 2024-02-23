package kr.pickple.back.crew.repository.entity;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.common.util.RegistrationStatusAttributeConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "crew_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Convert(converter = RegistrationStatusAttributeConverter.class)
    @Column(length = 10)
    private RegistrationStatus status = WAITING;

    @NotNull
    private Long memberId;

    @NotNull
    private Long crewId;

    @Builder
    private CrewMemberEntity(final RegistrationStatus status, final Long memberId, final Long crewId) {
        this.status = status;
        this.memberId = memberId;
        this.crewId = crewId;
    }
}
