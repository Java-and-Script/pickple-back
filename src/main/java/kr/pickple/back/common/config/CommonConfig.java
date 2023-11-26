package kr.pickple.back.common.config;

import kr.pickple.back.common.config.property.RedisProperties;
import kr.pickple.back.common.config.property.S3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {RedisProperties.class, S3Properties.class})
public class CommonConfig {

}
