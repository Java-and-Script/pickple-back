package kr.pickple.back.fixture.dto;

import kr.pickple.back.auth.dto.response.AccessTokenResponse;

public class AuthDtoFixtures {

    public static AccessTokenResponse accessTokenResponseBuild() {
        return AccessTokenResponse.of("accessToken");
    }
}
