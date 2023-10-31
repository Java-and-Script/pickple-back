package kr.pickple.back.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import kr.pickple.back.auth.config.property.CorsConfig;
import kr.pickple.back.auth.config.property.KakaoOAuthConfig;

@Configuration
@EnableConfigurationProperties(value = {KakaoOAuthConfig.class, CorsConfig.class})
public class AuthConfig {

}
