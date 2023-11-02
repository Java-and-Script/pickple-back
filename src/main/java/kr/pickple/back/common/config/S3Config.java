package kr.pickple.back.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3.default")
public class S3Config {

    private final String profile;
    private final String background;

    public S3Config(String profile, String background) {
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
