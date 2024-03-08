package kr.pickple.back.game.dto.response;

import kr.pickple.back.common.domain.RegistrationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMemberRegistrationStatusResponse {

    private final RegistrationStatus memberRegistrationStatus;
    private final Boolean isReviewDone;
}
