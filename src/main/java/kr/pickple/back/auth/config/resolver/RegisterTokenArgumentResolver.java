package kr.pickple.back.auth.config.resolver;

import static org.springframework.http.HttpHeaders.*;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import kr.pickple.back.auth.domain.token.JwtProvider;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegisterTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final TokenExtractor tokenExtractor;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.withContainingClass(String.class)
                .hasParameterAnnotation(SignUp.class);
    }

    @Override
    public String resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final String registerToken = tokenExtractor.extractRegisterToken(webRequest.getHeader(AUTHORIZATION));
        jwtProvider.validateRegisterToken(registerToken);

        return jwtProvider.getSubject(registerToken);
    }
}
