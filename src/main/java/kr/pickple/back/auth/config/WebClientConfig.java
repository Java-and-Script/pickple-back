package kr.pickple.back.auth.config;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.*;
import static org.springframework.http.MediaType.*;

import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebClientConfig {

    /**
     * WebClient로 외부 API 요청시 받아오는 json의 property가 SNAKE_CASE인 경우 CAMEL_CASE로 변경하기 위해 설정하는 설정 값입니다.
     * ObjectMapper를 이용하여 SNAKE_CASE 전략을 설정하고, FAIL_ON_UNKNOWN_PROPERTIES를 이용하여 Response로 받는 객체에서 없는 필드는
     * 받지 않도록 합니다. 해당 설정 값을 WebClient Builder의 exchangeStrategis에 설정하여 변환될 수 있도록 합니다.
     * @author 황창현
     */
    public static ExchangeStrategies getExchangeStrategies() {
        ObjectMapper objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(SNAKE_CASE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, APPLICATION_JSON)))
                .build();
    }
}
