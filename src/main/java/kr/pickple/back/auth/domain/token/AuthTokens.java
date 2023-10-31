package kr.pickple.back.auth.domain.token;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthTokens {

    private final String accessToken;
    private final String refreshToken;
}
