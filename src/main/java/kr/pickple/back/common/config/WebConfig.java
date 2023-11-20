package kr.pickple.back.common.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.pickple.back.auth.config.OauthProviderConverter;
import kr.pickple.back.auth.config.property.CorsProperties;
import kr.pickple.back.auth.config.resolver.LoginTokenArgumentResolver;
import kr.pickple.back.auth.config.resolver.RegisterTokenArgumentResolver;
import kr.pickple.back.chat.util.RoomTypeConverter;
import kr.pickple.back.common.util.RegistrationStatusConverter;
import kr.pickple.back.game.util.CategoryConverter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;
    private final RegisterTokenArgumentResolver registerTokenArgumentResolver;
    private final LoginTokenArgumentResolver loginTokenArgumentResolver;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getUrls())
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
        registry.addConverter(new OauthProviderConverter());
        registry.addConverter(new RegistrationStatusConverter());
        registry.addConverter(new CategoryConverter());
        registry.addConverter(new RoomTypeConverter());
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(registerTokenArgumentResolver);
        resolvers.add(loginTokenArgumentResolver);

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/static/docs/");
    }
}
