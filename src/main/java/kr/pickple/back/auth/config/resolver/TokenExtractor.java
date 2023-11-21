package kr.pickple.back.auth.config.resolver;

import static kr.pickple.back.auth.exception.AuthExceptionCode.*;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import kr.pickple.back.auth.exception.AuthException;
import kr.pickple.back.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenExtractor {

    private static final String BEARER_TYPE = "Bearer ";
    private static final String REFRESH_TOKEN = "refresh-token";

    private final RefreshTokenRepository refreshTokenRepository;

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

    public String extractRefreshToken(final Cookie... cookies) {
        if (cookies == null) {
            throw new AuthException(AUTH_NOT_FOUND_REFRESH_TOKEN);
        }

        return Arrays.stream(cookies)
                .filter(this::isValidRefreshToken)
                .findFirst()
                .orElseThrow(() -> new AuthException(AUTH_NOT_FOUND_REFRESH_TOKEN))
                .getValue();
    }

    private Boolean isValidRefreshToken(final Cookie cookie) {
        return REFRESH_TOKEN.equals(cookie.getName()) && refreshTokenRepository.existsById(cookie.getValue());
    }
}
