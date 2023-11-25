package kr.pickple.back.fixture.domain;

import java.time.LocalDateTime;

import kr.pickple.back.auth.domain.oauth.OauthMember;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.RefreshToken;

public class AuthFixtures {

    public static OauthMember oauthMemberBuild() {
        return OauthMember.builder()
                .oauthId(1L)
                .oauthProvider(OauthProvider.KAKAO)
                .email("pickple@pickple.kr")
                .profileImageUrl("https://amazon.com/pickple/1")
                .nickname("pickple")
                .build();
    }

    public static AuthTokens authTokensBuild() {
        return AuthTokens.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }

    public static RefreshToken refreshTokenBuild() {
        return RefreshToken.builder()
                .token("refreshToken")
                .memberId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
