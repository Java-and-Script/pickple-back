package kr.pickple.back.game.repository.entity;

import static java.lang.Boolean.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameMemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Convert(converter = RegistrationStatusAttributeConverter.class)
    @Column(length = 10)
    private RegistrationStatus status = WAITING;

    @NotNull
    private Boolean isReview = FALSE;

    @NotNull
    private Long memberId;

    @NotNull
    private Long gameId;

    @Builder
    private GameMemberEntity(final Long memberId, final Long gameId, final RegistrationStatus status) {
        this.status = status;
        this.memberId = memberId;
        this.gameId = gameId;
    }

    public void confirmRegistration() {
        this.status = CONFIRMED;
    }

    public void updateStatus(final RegistrationStatus status) {
        this.status = status;
    }

    public Boolean isStatusChangedFromWaitingToConfirmed(RegistrationStatus updateStatus) {
        return this.status == WAITING && updateStatus == CONFIRMED;
    }

    public Boolean isAlreadyReviewDone() {
        return isReview;
    }

    public void updateReviewDone() {
        this.isReview = TRUE;
    }
}
