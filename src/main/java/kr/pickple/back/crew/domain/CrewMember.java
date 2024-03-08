package kr.pickple.back.crew.domain;

import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.exception.CrewException;
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
public class CrewMember {

    private Long crewMemberId;
    private RegistrationStatus status;
    private Member member;
    private Crew crew;

    public void updateCrewMemberId(final Long crewMemberId) {
        this.crewMemberId = crewMemberId;
    }

    public void updateRegistrationStatus(final RegistrationStatus status) {
        if (this.status == status) {
            throw new CrewException(CREW_MEMBER_ALREADY_IN_THAT_REGISTRATION_STATUS, status);
        }

        this.status = status;
    }
}
