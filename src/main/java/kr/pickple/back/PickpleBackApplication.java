package kr.pickple.back;

import kr.pickple.back.common.config.S3Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(S3Config.class)
public class PickpleBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(PickpleBackApplication.class, args);
    }
}
