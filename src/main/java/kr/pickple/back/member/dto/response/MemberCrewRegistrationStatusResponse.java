package kr.pickple.back.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class MemberCrewRegistrationStatusResponse {

    private final Boolean registrationStatus;
}
