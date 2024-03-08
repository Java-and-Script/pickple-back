package kr.pickple.back.auth.service;

import static kr.pickple.back.auth.exception.AuthExceptionCode.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.config.resolver.TokenExtractor;
import kr.pickple.back.auth.domain.oauth.OauthMember;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.dto.response.AccessTokenResponse;
import kr.pickple.back.auth.exception.AuthException;
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.auth.service.authcode.AuthCodeRequestUrlProviderComposite;
import kr.pickple.back.auth.service.memberclient.OauthMemberClientComposite;
import kr.pickple.back.member.repository.entity.MemberEntity;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OauthService {

    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final MemberReader memberReader;
    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final TokenExtractor tokenExtractor;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RedisRepository redisRepository;
    private final AddressReader addressReader;

    public String getAuthCodeRequestUrl(final OauthProvider oauthProvider) {
        return authCodeRequestUrlProviderComposite.provide(oauthProvider);
    }

    @Transactional
    public AuthenticatedMemberResponse processLoginOrRegistration(
            final OauthProvider oauthProvider,
            final String authCode
    ) {
        final OauthMember oauthMember = oauthMemberClientComposite.fetch(oauthProvider, authCode);
        final Optional<MemberEntity> member = memberReader.readByOauthId(oauthMember.getOauthId());

        // 사용자가 로그인 하는 경우
        if (member.isPresent()) {
            final MemberEntity loginMember = member.get();
            final AuthTokens loginTokens = jwtProvider.createLoginToken(String.valueOf(loginMember.getId()));

            final RefreshToken refreshToken = RefreshToken.builder()
                    .token(loginTokens.getRefreshToken())
                    .memberId(loginMember.getId())
                    .createdAt(LocalDateTime.now())
                    .build();

            redisRepository.saveHash(
                    REFRESH_TOKEN_KEY,
                    refreshToken.getToken(),
                    refreshToken,
                    jwtProperties.getRefreshTokenExpirationTime()
            );

            final MainAddress mainAddress = addressReader.readMainAddressByIds(
                    loginMember.getAddressDepth1Id(),
                    loginMember.getAddressDepth2Id()
            );

            return AuthenticatedMemberResponse.of(loginMember, loginTokens, mainAddress);
        }

        // 사용자가 회원 가입 시, 추가 정보(주 활동지역, 포지션)를 입력하는 경우
        final String oauthProviderName = oauthMember.getOauthProvider().name();
        final AuthTokens registerToken = jwtProvider.createRegisterToken(oauthProviderName + oauthMember.getOauthId());

        return AuthenticatedMemberResponse.of(oauthMember, registerToken);
    }

    public AccessTokenResponse regenerateAccessToken(final String refreshToken, final String authorizationHeader) {
        final String accessToken = tokenExtractor.extractAccessToken(authorizationHeader);

        if (jwtProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken)) {
            final RefreshToken validRefreshToken = redisRepository.findHash(REFRESH_TOKEN_KEY, refreshToken);

            if (validRefreshToken == null) {
                throw new AuthException(AUTH_INVALID_REFRESH_TOKEN);
            }

            final String regeneratedAccessToken = jwtProvider.regenerateAccessToken(
                    validRefreshToken.getMemberId().toString());

            return AccessTokenResponse.of(regeneratedAccessToken);
        }

        if (jwtProvider.isValidRefreshAndValidAccess(refreshToken, accessToken)) {
            return AccessTokenResponse.of(accessToken);
        }

        throw new AuthException(AUTH_FAIL_TO_VALIDATE_TOKEN);
    }

    public void deleteRefreshToken(final String refreshToken) {
        redisRepository.deleteHash(REFRESH_TOKEN_KEY, refreshToken);
    }
}
