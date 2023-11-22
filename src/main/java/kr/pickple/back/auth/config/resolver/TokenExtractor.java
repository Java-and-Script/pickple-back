package kr.pickple.back.auth.config.resolver;

import static kr.pickple.back.auth.exception.AuthExceptionCode.*;

import org.springframework.stereotype.Component;

import kr.pickple.back.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private static final String BEARER_TYPE = "Bearer ";

    public String extractRegisterToken(final String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length()).trim();
        }

        throw new AuthException(AUTH_INVALID_REGISTER_TOKEN, header);
    }

    public String extractAccessToken(final String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length()).trim();
        }

        throw new AuthException(AUTH_INVALID_ACCESS_TOKEN, header);
    }
}
