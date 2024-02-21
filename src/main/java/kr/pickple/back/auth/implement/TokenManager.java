package kr.pickple.back.auth.implement;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.repository.RedisRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenManager {

    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RedisRepository redisRepository;

    public AuthTokens create(final Long memberId) {
        final AuthTokens loginTokens = jwtProvider.createLoginToken(String.valueOf(memberId));

        final RefreshToken refreshToken = RefreshToken.builder()
                .token(loginTokens.getRefreshToken())
                .memberId(memberId)
                .createdAt(LocalDateTime.now())
                .build();

        redisRepository.saveHash(
                REFRESH_TOKEN_KEY,
                refreshToken.getToken(),
                refreshToken,
                jwtProperties.getRefreshTokenExpirationTime()
        );

        return loginTokens;
    }
}
