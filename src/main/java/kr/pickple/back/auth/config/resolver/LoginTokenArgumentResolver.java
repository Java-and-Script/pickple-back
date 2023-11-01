package kr.pickple.back.auth.config.resolver;

import static org.springframework.http.HttpHeaders.*;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final TokenExtractor tokenExtractor;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.withContainingClass(Long.class)
                .hasParameterAnnotation(Login.class);
    }

    @Override
    public Long resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final Cookie[] cookies = webRequest.getNativeRequest(HttpServletRequest.class)
                .getCookies();

        final String accessToken = tokenExtractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));
        final String refreshToken = tokenExtractor.extractRefreshToken(cookies);

        final AuthTokens authTokens = AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        jwtProvider.validateTokens(authTokens);

        return Long.parseLong(jwtProvider.getSubject(accessToken));
    }
}
