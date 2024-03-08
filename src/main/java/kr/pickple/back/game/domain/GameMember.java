package kr.pickple.back.game.domain;

import static java.lang.Boolean.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMember {

    private Long gameMemberId;
    private RegistrationStatus status;
    private Member member;
    private Game game;
    private Boolean isReview = FALSE;

    public void updateGameMemberId(final Long gameMemberId) {
        this.gameMemberId = gameMemberId;
    }

    public void updateRegistrationStatus(final RegistrationStatus status) {
        this.status = status;
    }

    public Boolean isAlreadyReviewDone() {
        return isReview;
    }

    public Boolean isStatusChangedFromWaitingToConfirmed(RegistrationStatus updateStatus) {
        return this.status == WAITING && updateStatus == CONFIRMED;
    }
}
