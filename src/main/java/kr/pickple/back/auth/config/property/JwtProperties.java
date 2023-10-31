package kr.pickple.back.auth.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final String secretKey;
    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;
    private final Long registerTokenExpirationTime;
}
