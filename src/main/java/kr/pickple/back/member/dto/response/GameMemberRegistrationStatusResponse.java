package kr.pickple.back.member.dto.response;

import static java.lang.Boolean.*;

import kr.pickple.back.common.domain.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class GameMemberRegistrationStatusResponse {

    private final RegistrationStatus memberRegistrationStatus;
    private final Boolean isReview;
}
