package kr.pickple.back.common.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    private final String host;
    private final Integer port;
    private final String password;
}
