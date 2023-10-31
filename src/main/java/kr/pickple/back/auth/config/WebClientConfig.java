package kr.pickple.back.auth.config;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.*;
import static org.springframework.http.MediaType.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebClientConfig {

    @Bean
    public ExchangeStrategies getExchangeStrategies() {
        ObjectMapper objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(SNAKE_CASE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, APPLICATION_JSON)))
                .build();
    }
}
