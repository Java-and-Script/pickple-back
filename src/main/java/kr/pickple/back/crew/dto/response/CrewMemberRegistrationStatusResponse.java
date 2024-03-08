package kr.pickple.back.crew.dto.response;

import kr.pickple.back.common.domain.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class CrewMemberRegistrationStatusResponse {

    private final RegistrationStatus memberRegistrationStatus;
}
