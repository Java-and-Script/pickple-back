package kr.pickple.back.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import kr.pickple.back.common.config.property.AsyncProperties;
import kr.pickple.back.common.config.property.RedisProperties;
import kr.pickple.back.common.config.property.S3Properties;

@Configuration
@EnableConfigurationProperties(value = {RedisProperties.class, S3Properties.class, AsyncProperties.class})
public class CommonConfig {

}


