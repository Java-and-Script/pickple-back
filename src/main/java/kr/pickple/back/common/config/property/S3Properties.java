package kr.pickple.back.common.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "s3.default")
public class S3Properties {

    private final String crewProfile;
    private final String crewBackground;
}
