package kr.pickple.back.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.servlet.http.Cookie;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.dto.response.AccessTokenResponse;
import kr.pickple.back.auth.service.OauthService;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String BASE_URL = "/auth";
    private static final String OAUTH_PROVIDER = "kakao";
    private static final String AUTH_CODE = "authCode";

    @MockBean
    private OauthService oauthService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("oauth 제공자를 받으면 로그인 페이지로 리다이렉트 시킬 수 있다.")
    void redirectOauthLoginPage() throws Exception {
        // given
        final String REDIRECT_URL = "https://test.url";

        given(oauthService.getAuthCodeRequestUrl(any(OauthProvider.class))).willReturn(REDIRECT_URL);

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{oauthProvider}", OAUTH_PROVIDER));

        // then
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_URL));
    }

    @Test
    @DisplayName("oauth를 이용하여 로그인 할 수 있다.")
    void oauthLogin_ReturnAuthenticated() throws Exception {
        // given
        final AuthenticatedMemberResponse authenticatedMemberResponse = MemberDtoFixtures.authenticatedMemberResponseLoginBuild();

        given(oauthService.processLoginOrRegistration(any(OauthProvider.class), anyString())).willReturn(
                authenticatedMemberResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        get(BASE_URL + "/login/{oauthProvider}", OAUTH_PROVIDER).param("authCode", AUTH_CODE))
                .andExpect(status().isOk());

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(cookie().httpOnly("refresh-token", true))
                .andExpect(cookie().secure("refresh-token", true))
                .andExpect(cookie().exists("refresh-token"))
                .andExpect(jsonPath("accessToken").value(authenticatedMemberResponse.getAccessToken()))
                .andExpect(jsonPath("refreshToken").value(authenticatedMemberResponse.getRefreshToken()))
                .andExpect(jsonPath("id").value(authenticatedMemberResponse.getId()))
                .andExpect(jsonPath("nickname").value(authenticatedMemberResponse.getNickname()))
                .andExpect(jsonPath("profileImageUrl").value(authenticatedMemberResponse.getProfileImageUrl()))
                .andExpect(jsonPath("email").value(authenticatedMemberResponse.getEmail()))
                .andExpect(jsonPath("oauthId").value(authenticatedMemberResponse.getOauthId()))
                .andExpect(jsonPath("oauthProvider").value(authenticatedMemberResponse.getOauthProvider().name()))
                .andExpect(jsonPath("addressDepth1").value(authenticatedMemberResponse.getAddressDepth1()))
                .andExpect(jsonPath("addressDepth2").value(authenticatedMemberResponse.getAddressDepth2()));
    }

    @Test
    @DisplayName("oauth를 이용하여 로그인시 회원 정보가 존재하지 않을 경우 회원가입 관련 정보를 응답한다.")
    void oauthLogin_ReturnRegistration() throws Exception {
        // given
        final AuthenticatedMemberResponse authenticatedMemberResponse = MemberDtoFixtures.authenticatedMemberResponseRegistrationBuild();

        given(oauthService.processLoginOrRegistration(any(OauthProvider.class), anyString())).willReturn(
                authenticatedMemberResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        get(BASE_URL + "/login/{oauthProvider}", OAUTH_PROVIDER).param("authCode", AUTH_CODE))
                .andExpect(status().isOk());

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("accessToken").value(authenticatedMemberResponse.getAccessToken()))
                .andExpect(jsonPath("nickname").value(authenticatedMemberResponse.getNickname()))
                .andExpect(jsonPath("profileImageUrl").value(authenticatedMemberResponse.getProfileImageUrl()))
                .andExpect(jsonPath("email").value(authenticatedMemberResponse.getEmail()))
                .andExpect(jsonPath("oauthId").value(authenticatedMemberResponse.getOauthId()))
                .andExpect(jsonPath("oauthProvider").value(authenticatedMemberResponse.getOauthProvider().name()));
    }

    @Test
    @DisplayName("accessToken이 만료되었을 때 새로 갱신 할 수 있다.")
    void regenerateAccessToken() throws Exception {
        // given
        final Long memberId = 1L;
        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(memberId));
        final String regeneratedAccessToken = jwtProvider.regenerateAccessToken(String.valueOf(memberId));
        final AccessTokenResponse accessTokenResponse = AccessTokenResponse.of(regeneratedAccessToken);

        given(oauthService.regenerateAccessToken(anyString(), anyString())).willReturn(accessTokenResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/refresh")
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                        .cookie(new Cookie("refresh-token", authTokens.getRefreshToken())));

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("accessToken").value(accessTokenResponse.getAccessToken()));
    }

    @Test
    @DisplayName("로그인된 사용자는 로그아웃을 할 수 있다.")
    void logout() throws Exception {
        // given
        final Long memberId = 1L;
        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(memberId));

        // when
        final ResultActions resultActions = mockMvc.perform(
                delete(BASE_URL + "/logout")
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                        .cookie(new Cookie("refresh-token", authTokens.getRefreshToken())));

        // then
        resultActions.andExpect(status().isNoContent())
                .andExpect(cookie().httpOnly("refresh-token", true))
                .andExpect(cookie().secure("refresh-token", true))
                .andExpect(cookie().exists("refresh-token"))
                .andExpect(cookie().maxAge("refresh-token", 0));
    }
}
