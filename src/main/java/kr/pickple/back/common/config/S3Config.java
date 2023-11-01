package kr.pickple.back.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    private final String profile;
    private final String background;

    public S3Config(
            @Value("${s3.default.profile}") String profile,
            @Value("${s3.default.background}") String background
    ) {
        this.profile = profile;
        this.background = background;
    }

    public String getProfile() {
        return profile;
    }

    public String getBackground() {
        return background;
    }
}
