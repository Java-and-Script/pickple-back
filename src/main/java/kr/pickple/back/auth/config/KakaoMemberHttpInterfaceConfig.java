package kr.pickple.back.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import kr.pickple.back.auth.config.property.KakaoOAuthConfig;
import kr.pickple.back.auth.service.memberclient.KakaoMemberApiClient;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KakaoMemberHttpInterfaceConfig {

    private final KakaoOAuthConfig kakaoOauthConfig;

    @Bean
    public KakaoMemberApiClient kakaoMemberApiClient() {
        return createHttpInterface(KakaoMemberApiClient.class);
    }

    private <T> T createHttpInterface(final Class<T> clazz) {
        final WebClient webClient = WebClient.builder()
                .baseUrl(kakaoOauthConfig.getMemberUrl())
                .build();
        final HttpServiceProxyFactory build = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build();

        return build.createClient(clazz);
    }
}
