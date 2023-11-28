package kr.pickple.back.common.config.property;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ConfigurationProperties(prefix = "async")
public class AsyncProperties {

    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
}
