package kr.pickple.back.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.config.resolver.TokenExtractor;
import kr.pickple.back.auth.domain.oauth.OauthMember;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.dto.response.AccessTokenResponse;
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.auth.service.authcode.AuthCodeRequestUrlProviderComposite;
import kr.pickple.back.auth.service.memberclient.OauthMemberClientComposite;
import kr.pickple.back.fixture.domain.AddressFixtures;
import kr.pickple.back.fixture.domain.AuthFixtures;
import kr.pickple.back.fixture.domain.MemberFixtures;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final OauthProvider OAUTH_PROVIDER = OauthProvider.KAKAO;
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    @InjectMocks
    private OauthService oauthService;

    @Mock
    private AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;

    @Mock
    private OauthMemberClientComposite oauthMemberClientComposite;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private TokenExtractor tokenExtractor;

    @Test
    @DisplayName("oauth 제공자를 받으면 Oauth 로그인 페이지를 반환한다.")
    void redirectOauthLoginPage() {
        // given
        final String redirectUrl = "http://test.url";

        given(authCodeRequestUrlProviderComposite.provide(OAUTH_PROVIDER)).willReturn(redirectUrl);

        // when
        final String oauthRedirectUrl = oauthService.getAuthCodeRequestUrl(OAUTH_PROVIDER);

        // then
        assertThat(oauthRedirectUrl).isEqualTo(redirectUrl);
    }

    @Test
    @DisplayName("oauth를 이용하여 로그인 할 수 있다.")
    void oauthLogin_ReturnAuthenticated() {
        // given
        final String authCode = "authCode";
        final OauthMember oauthMember = AuthFixtures.oauthMemberBuild();
        final AddressDepth1 addressDepth1 = AddressFixtures.addressDepth1Build();
        final AddressDepth2 addressDepth2 = AddressFixtures.addressDepth2Build();
        final Member member = MemberFixtures.memberBuild(addressDepth1, addressDepth2);
        final AuthTokens loginTokens = AuthFixtures.authTokensBuild();
        final Long refreshTokenExpirationTime = 10000L;

        given(memberRepository.findByOauthId(anyLong())).willReturn(Optional.ofNullable(member));
        given(oauthMemberClientComposite.fetch(any(OauthProvider.class), anyString())).willReturn(oauthMember);
        given(jwtProvider.createLoginToken(anyString())).willReturn(loginTokens);
        given(jwtProperties.getRefreshTokenExpirationTime()).willReturn(refreshTokenExpirationTime);

        // when
        AuthenticatedMemberResponse authenticatedMemberResponse = oauthService.processLoginOrRegistration(
                OAUTH_PROVIDER, authCode);

        // then
        assertThat(authenticatedMemberResponse.getOauthId()).isEqualTo(member.getOauthId());
        assertThat(authenticatedMemberResponse.getOauthProvider()).isEqualTo(member.getOauthProvider());
        assertThat(authenticatedMemberResponse.getEmail()).isEqualTo(member.getEmail());
        assertThat(authenticatedMemberResponse.getNickname()).isEqualTo(member.getNickname());
        assertThat(authenticatedMemberResponse.getProfileImageUrl()).isEqualTo(member.getProfileImageUrl());
        assertThat(authenticatedMemberResponse.getAccessToken()).isEqualTo(loginTokens.getAccessToken());
        assertThat(authenticatedMemberResponse.getRefreshToken()).isEqualTo(loginTokens.getRefreshToken());
        assertThat(authenticatedMemberResponse.getAddressDepth1()).isEqualTo(addressDepth1.getName());
        assertThat(authenticatedMemberResponse.getAddressDepth2()).isEqualTo(addressDepth2.getName());

        verify(memberRepository).findByOauthId(anyLong());
        verify(oauthMemberClientComposite).fetch(any(OauthProvider.class), anyString());
        verify(jwtProvider).createLoginToken(anyString());
        verify(jwtProperties).getRefreshTokenExpirationTime();
        verify(redisRepository).saveHash(anyString(), anyString(), any(), anyLong());
    }

    @Test
    @DisplayName("oauth를 이용하여 로그인시 회원 정보가 존재하지 않을 경우 회원가입 관련 정보를 응답한다.")
    void oauthLogin_ReturnRegistration() {
        // given
        final String authCode = "authCode";
        final OauthMember oauthMember = AuthFixtures.oauthMemberBuild();
        final Member member = null;
        final AuthTokens registerToken = AuthFixtures.authTokensBuild();

        given(memberRepository.findByOauthId(anyLong())).willReturn(Optional.ofNullable(member));
        given(oauthMemberClientComposite.fetch(any(OauthProvider.class), anyString())).willReturn(oauthMember);
        given(jwtProvider.createRegisterToken(anyString())).willReturn(registerToken);

        // when
        final AuthenticatedMemberResponse authenticatedMemberResponse = oauthService.processLoginOrRegistration(
                OAUTH_PROVIDER, authCode);

        // then
        assertThat(authenticatedMemberResponse.getOauthId()).isEqualTo(oauthMember.getOauthId());
        assertThat(authenticatedMemberResponse.getOauthProvider()).isEqualTo(oauthMember.getOauthProvider());
        assertThat(authenticatedMemberResponse.getEmail()).isEqualTo(oauthMember.getEmail());
        assertThat(authenticatedMemberResponse.getNickname()).isEqualTo(oauthMember.getNickname());
        assertThat(authenticatedMemberResponse.getProfileImageUrl()).isEqualTo(oauthMember.getProfileImageUrl());
        assertThat(authenticatedMemberResponse.getAccessToken()).isEqualTo(registerToken.getAccessToken());
        assertThat(authenticatedMemberResponse.getId()).isNull();
        assertThat(authenticatedMemberResponse.getRefreshToken()).isNull();
        assertThat(authenticatedMemberResponse.getAddressDepth1()).isNull();
        assertThat(authenticatedMemberResponse.getAddressDepth2()).isNull();

        verify(memberRepository).findByOauthId(anyLong());
        verify(oauthMemberClientComposite).fetch(any(OauthProvider.class), anyString());
        verify(jwtProvider).createRegisterToken(anyString());
    }

    @Test
    @DisplayName("accessToken이 만료되었을 때 새로 갱신 할 수 있다.")
    void regenerateAccessToken() {
        // given
        final AuthTokens authTokens = AuthFixtures.authTokensBuild();
        final String authorizationHeader = "Bearer " + authTokens.getAccessToken();
        final RefreshToken refreshToken = AuthFixtures.refreshTokenBuild();
        final String regeneratedAccessToken = AuthFixtures.authTokensBuild().getAccessToken();

        given(tokenExtractor.extractAccessToken(authorizationHeader)).willReturn(authTokens.getAccessToken());
        given(jwtProvider.isValidRefreshAndInvalidAccess(authTokens.getRefreshToken(),
                authTokens.getAccessToken())).willReturn(true);
        given(redisRepository.findHash(REFRESH_TOKEN_KEY, authTokens.getRefreshToken())).willReturn(refreshToken);
        given(jwtProvider.regenerateAccessToken(String.valueOf(1L))).willReturn(regeneratedAccessToken);

        // when
        final AccessTokenResponse accessTokenResponse = oauthService.regenerateAccessToken(authTokens.getRefreshToken(),
                authorizationHeader);

        // then
        assertThat(accessTokenResponse.getAccessToken()).isEqualTo(regeneratedAccessToken);

        verify(tokenExtractor).extractAccessToken(anyString());
        verify(jwtProvider).isValidRefreshAndInvalidAccess(anyString(), anyString());
        verify(redisRepository).findHash(anyString(), anyString());
        verify(jwtProvider).regenerateAccessToken(anyString());
    }

    @Test
    @DisplayName("로그인된 사용자는 로그아웃을 할 수 있다.")
    void logout() {
        // given && when
        final AuthTokens authTokens = AuthFixtures.authTokensBuild();
        oauthService.deleteRefreshToken(authTokens.getRefreshToken());

        // then
        verify(redisRepository).deleteHash(REFRESH_TOKEN_KEY, authTokens.getRefreshToken());
    }
}
