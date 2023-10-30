package kr.pickple.back.auth.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOAuthConfig {

    private final String authUrl;
    private final String redirectUrl;
    private final String clientId;
    private final String clientSecret;
    private final String[] scope;
}
