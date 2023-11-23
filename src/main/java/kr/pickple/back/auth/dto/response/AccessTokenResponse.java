package kr.pickple.back.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class AccessTokenResponse {

    private String accessToken;
}
