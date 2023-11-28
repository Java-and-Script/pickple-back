package kr.pickple.back.member.dto.response;

import kr.pickple.back.member.domain.MemberRegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class MemberRegistrationStatusResponse {

    private final MemberRegistrationStatus memberRegistrationStatus;
}
