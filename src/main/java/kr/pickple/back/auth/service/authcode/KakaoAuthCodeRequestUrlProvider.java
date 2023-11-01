package kr.pickple.back.auth.service.authcode;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import kr.pickple.back.auth.config.property.KakaoOauthProperties;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

    private final KakaoOauthProperties kakaoOauthProperties;

    @Override
    public OauthProvider oauthprovider() {
        return OauthProvider.KAKAO;
    }

    @Override
    public String provideUrl() {
        return UriComponentsBuilder
                .fromUriString(kakaoOauthProperties.getProviderUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoOauthProperties.getClientId())
                .queryParam("redirect_uri", kakaoOauthProperties.getRedirectUrl())
                .queryParam("scope", String.join(",", kakaoOauthProperties.getScope()))
                .toUriString();
    }
}
