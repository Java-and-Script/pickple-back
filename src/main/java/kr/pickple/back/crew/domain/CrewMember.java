package kr.pickple.back.crew.domain;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import kr.pickple.back.common.domain.RegistrationStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CrewMember {

    private Long crewMemberId;
    private RegistrationStatus status = WAITING;
    private Long memberId;
    private Long crewId;

    @Builder
    private CrewMember(final Long memberId, final Long crewId) {
        this.memberId = memberId;
        this.crewId = crewId;
    }

    public void updateCrewMemberId(final Long crewMemberId) {
        this.crewMemberId = crewMemberId;
    }

    public void confirmRegistration() {
        this.status = CONFIRMED;
    }
}
