package kr.pickple.back.auth.service.authcode;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import kr.pickple.back.auth.config.property.KakaoOAuthProperties;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

    private final KakaoOAuthProperties kakaoOAuthProperties;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String provideUrl() {
        return UriComponentsBuilder
                .fromUriString(kakaoOAuthProperties.getProviderUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoOAuthProperties.getClientId())
                .queryParam("redirect_uri", kakaoOAuthProperties.getRedirectUrl())
                .queryParam("scope", String.join(",", kakaoOAuthProperties.getScope()))
                .toUriString();
    }
}
