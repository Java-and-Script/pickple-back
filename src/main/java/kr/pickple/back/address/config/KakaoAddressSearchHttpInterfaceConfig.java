package kr.pickple.back.address.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import kr.pickple.back.address.service.kakao.KakaoAddressSearchApiClient;
import kr.pickple.back.auth.config.WebClientConfig;
import kr.pickple.back.auth.config.property.KakaoOauthProperties;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KakaoAddressSearchHttpInterfaceConfig {

    private final KakaoOauthProperties kakaoOauthProperties;

    @Bean
    public KakaoAddressSearchApiClient kakaoAddressSearchApiClient() {
        return createHttpInterface(KakaoAddressSearchApiClient.class);
    }

    private <T> T createHttpInterface(final Class<T> clazz) {
        final WebClient webClient = WebClient.builder()
                .baseUrl(kakaoOauthProperties.getAddressUrl())
                .exchangeStrategies(WebClientConfig.getExchangeStrategies())
                .build();
        final HttpServiceProxyFactory build = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build();

        return build.createClient(clazz);
    }
}
