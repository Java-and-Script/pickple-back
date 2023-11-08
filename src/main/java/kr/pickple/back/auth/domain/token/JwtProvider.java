package kr.pickple.back.auth.domain.token;

import static kr.pickple.back.auth.exception.AuthExceptionCode.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.exception.AuthException;

@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtProvider(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        final byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public AuthTokens createLoginToken(final String subject) {
        final String accessToken = generateToken(subject, jwtProperties.getAccessTokenExpirationTime());
        final String refreshToken = generateToken("", jwtProperties.getRefreshTokenExpirationTime());

        return AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthTokens createRegisterToken(final String subject) {
        String registerToken = generateToken(subject, jwtProperties.getRegisterTokenExpirationTime());

        return AuthTokens.builder()
                .accessToken(registerToken)
                .build();
    }

    private String generateToken(final String subject, final Long expirationTime) {
        final Date now = new Date();

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String regenerateAccessToken(final String subject) {
        return generateToken(subject, jwtProperties.getAccessTokenExpirationTime());
    }

    public String getSubject(final String token) {
        return parseToken(token)
                .getPayload()
                .getSubject();
    }

    private Jws<Claims> parseToken(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public void validateTokens(final AuthTokens authTokens) {
        validateAccessToken(authTokens.getAccessToken());
        validateRefreshToken(authTokens.getRefreshToken());
    }

    public void validateAccessToken(final String accessToken) {
        try {
            parseToken(accessToken);
        } catch (final ExpiredJwtException e) {
            throw new AuthException(AUTH_EXPIRED_ACCESS_TOKEN, accessToken);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new AuthException(AUTH_INVALID_ACCESS_TOKEN, accessToken);
        }
    }

    private void validateRefreshToken(final String refreshToken) {
        try {
            parseToken(refreshToken);
        } catch (final ExpiredJwtException e) {
            throw new AuthException(AUTH_EXPIRED_REFRESH_TOKEN, refreshToken);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new AuthException(AUTH_INVALID_REFRESH_TOKEN, refreshToken);
        }
    }

    public void validateRegisterToken(final String registerToken) {
        try {
            parseToken(registerToken);
        } catch (final ExpiredJwtException e) {
            throw new AuthException(AUTH_EXPIRED_REGISTER_TOKEN, registerToken);
        } catch (final JwtException | IllegalArgumentException e) {
            throw new AuthException(AUTH_INVALID_REGISTER_TOKEN, registerToken);
        }
    }

    public boolean isValidRefreshAndInvalidAccess(final String refreshToken, final String accessToken) {
        validateRefreshToken(refreshToken);

        try {
            validateAccessToken(accessToken);
        } catch (final AuthException e) {
            return true;
        }

        return false;
    }

    public boolean isValidRefreshAndValidAccess(final String refreshToken, final String accessToken) {
        try {
            validateRefreshToken(refreshToken);
            validateAccessToken(accessToken);

            return true;
        } catch (final JwtException e) {
            return false;
        }
    }
}
