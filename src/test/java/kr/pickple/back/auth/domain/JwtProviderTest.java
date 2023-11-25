package kr.pickple.back.auth.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;

@SpringBootTest
class JwtProviderTest {

    private static final Long MEMBER_ID = 1L;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("memberId로 accessToken과 refreshToken을 만들 수 있다.")
    void create_AuthTokens() {
        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(MEMBER_ID));

        assertThat(authTokens.getAccessToken()).isNotNull();
        assertThat(authTokens.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("oauthId와 oauthProvider로 registerToken을 만들 수 있다.")
    void create_RegisterToken() {
        final Long oauthId = 1L;
        final OauthProvider oauthProvider = OauthProvider.KAKAO;
        final AuthTokens registerToken = jwtProvider.createRegisterToken(
                oauthProvider.name() + oauthId);

        assertThat(registerToken.getAccessToken()).isNotNull();
        assertThat(registerToken.getRefreshToken()).isNull();
    }
}
