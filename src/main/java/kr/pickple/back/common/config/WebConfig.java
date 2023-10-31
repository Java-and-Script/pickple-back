package kr.pickple.back.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.pickple.back.auth.config.property.CorsProperties;
import kr.pickple.back.auth.util.OAuthProviderConverter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getUrl())
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name()
                )
                .allowCredentials(true)
                .exposedHeaders("*");
    }

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(new OAuthProviderConverter());
    }
}
