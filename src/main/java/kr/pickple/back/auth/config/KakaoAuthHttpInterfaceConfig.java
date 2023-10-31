package kr.pickple.back.auth.config;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import kr.pickple.back.auth.config.property.KakaoOAuthConfig;
import kr.pickple.back.auth.service.memberclient.KakaoAuthApiClient;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KakaoAuthHttpInterfaceConfig {

    private final KakaoOAuthConfig kakaoOauthConfig;

    @Bean
    public KakaoAuthApiClient kakaoAuthApiClient() {
        return createHttpInterface(KakaoAuthApiClient.class);
    }

    private <T> T createHttpInterface(final Class<T> clazz) {
        final WebClient webClient = WebClient.builder()
                .defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .baseUrl(kakaoOauthConfig.getAuthUrl())
                .build();
        final HttpServiceProxyFactory build = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build();

        return build.createClient(clazz);
    }
}
