package kr.pickple.back.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import kr.pickple.back.auth.config.property.CorsProperties;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.config.property.KakaoOauthProperties;

@Configuration
@EnableConfigurationProperties(value = {KakaoOauthProperties.class, CorsProperties.class, JwtProperties.class})
public class AuthConfig {

}
