package kr.pickple.back.auth.domain.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthTokens {

    private String accessToken;
    private String refreshToken;
}
