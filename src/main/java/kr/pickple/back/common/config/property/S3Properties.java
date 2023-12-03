package kr.pickple.back.common.config.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "s3.default")
public class S3Properties {

    private final String crewProfile;
    private final String crewBackground;
}
