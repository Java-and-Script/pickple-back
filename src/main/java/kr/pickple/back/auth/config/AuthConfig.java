package kr.pickple.back.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import kr.pickple.back.auth.config.property.CorsProperties;
import kr.pickple.back.auth.config.property.JwtConfig;
import kr.pickple.back.auth.config.property.KakaoOAuthProperties;

@Configuration
@EnableConfigurationProperties(value = {KakaoOAuthProperties.class, CorsProperties.class, JwtConfig.class})
public class AuthConfig {

}
