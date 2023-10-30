package kr.pickple.back.auth.service.authcode;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import kr.pickple.back.auth.config.property.KakaoOAuthConfig;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

    private final KakaoOAuthConfig kakaoOAuthConfig;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String provideUrl() {
        return UriComponentsBuilder
                .fromUriString(kakaoOAuthConfig.getAuthUrl() + "/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoOAuthConfig.getClientId())
                .queryParam("redirect_uri", kakaoOAuthConfig.getRedirectUrl())
                .queryParam("scope", String.join(",", kakaoOAuthConfig.getScope()))
                .toUriString();
    }
}
