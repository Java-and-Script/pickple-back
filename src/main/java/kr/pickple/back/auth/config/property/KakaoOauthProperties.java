package kr.pickple.back.auth.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOauthProperties {

    private final String clientId;
    private final String clientSecret;
    private final String[] scope;
    private final String authUrl;
    private final String redirectUrl;
    private final String memberUrl;
    private final String addressUrl;
    private final String providerUrl;
}
