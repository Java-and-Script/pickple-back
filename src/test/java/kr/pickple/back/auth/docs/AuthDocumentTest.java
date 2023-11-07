package kr.pickple.back.auth.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import jakarta.servlet.http.Cookie;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.dto.response.AccessTokenResponse;
import kr.pickple.back.auth.service.OauthService;
import kr.pickple.back.fixture.dto.AuthDtoFixtures;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AuthDocumentTest {

    @MockBean
    private OauthService oauthService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("oauth 제공자를 받으면 로그인 페이지로 리다이렉트 시킬 수 있다.")
    void redirectOauthLoginPage() throws Exception {
        // given
        final String REDIRECT_URL = "https://test.url";

        given(oauthService.getAuthCodeRequestUrl(any(OauthProvider.class))).willReturn(REDIRECT_URL);

        // when
        final ResultActions resultActions = mockMvc.perform(get("/auth/{oauthProvider}", "kakao"))
                .andExpect(status().is3xxRedirection());

        // then
        resultActions.andDo(document("redirect-oauth-login-page",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .summary("oauth 페이지로 리다이렉트")
                                        .description("클라이언트에서 로그인 버튼을 클릭하면 oauth 페이지로 리다이렉트 한다.")
                                        .pathParameters(
                                                parameterWithName("oauthProvider")
                                                        .defaultValue("kakao")
                                                        .type(SimpleType.STRING)
                                                        .description("oauth 제공자")
                                        )
                                        .responseHeaders(
                                                headerWithName("Location").defaultValue(REDIRECT_URL).description("리다이렉트 주소"))
                                        .build()
                        )
                )
        );
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
                        get("/auth/login/{oauthProvider}", "kakao").param("authCode", "testCode"))
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("oauth-login-authenticated",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .summary("oauth 로그인")
                                        .description("oauth를 이용하여 로그인 할 수 있다.")
                                        .responseSchema(schema("Authenticated"))
                                        .pathParameters(
                                                parameterWithName("oauthProvider")
                                                        .defaultValue("kakao")
                                                        .type(SimpleType.STRING)
                                                        .description("oauth 제공자")
                                        )
                                        .queryParameters(
                                                parameterWithName("authCode")
                                                        .defaultValue("testCode")
                                                        .type(SimpleType.STRING)
                                                        .description("oauth 로그인 후 받은 authCode")
                                        )
                                        .responseFields(
                                                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                                        .description("AccessToken"),
                                                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                                                        .description("RefreshToken"),
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("oauthId").type(JsonFieldType.NUMBER).description("oauthId"),
                                                fieldWithPath("oauthProvider").type(JsonFieldType.STRING)
                                                        .description("oauth 제공자"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.STRING)
                                                        .description("주소2(도,시)")
                                        )
                                        .responseHeaders(
                                                headerWithName("Set-Cookie").description("refreshToken")
                                        )
                                        .build()
                        )
                )
        ).andDo(print());
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
                        get("/auth/login/{oauthProvider}", "kakao").param("authCode", "testCode"))
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("oauth-login-registration",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .summary("oauth 회원가입")
                                        .description("oauth를 이용하여 로그인시 회원 정보가 존재하지 않을 경우 회원가입 관련 정보를 응답한다.")
                                        .responseSchema(schema("Registration"))
                                        .pathParameters(
                                                parameterWithName("oauthProvider")
                                                        .defaultValue("kakao")
                                                        .type(SimpleType.STRING)
                                                        .description("oauth 제공자")
                                        )
                                        .queryParameters(
                                                parameterWithName("authCode")
                                                        .defaultValue("testCode")
                                                        .type(SimpleType.STRING)
                                                        .description("oauth 로그인 후 받은 authCode")
                                        )
                                        .responseFields(
                                                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                                        .description("AccessToken"),
                                                fieldWithPath("refreshToken").type(JsonFieldType.NULL)
                                                        .description("RefreshToken"),
                                                fieldWithPath("id").type(JsonFieldType.NULL).description("회원 ID"),
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("oauthId").type(JsonFieldType.NUMBER).description("oauthId"),
                                                fieldWithPath("oauthProvider").type(JsonFieldType.STRING)
                                                        .description("oauth 제공자"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.NULL)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.NULL)
                                                        .description("주소2(도,시)")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("accessToken이 만료되었을 때 새로 갱신 할 수 있다.")
    void regenerateAccessToken() throws Exception {
        // given
        final AccessTokenResponse accessTokenResponse = AuthDtoFixtures.accessTokenResponseBuild();
        final String accessTokenRequest = "accessToken";

        given(oauthService.regenerateAccessToken(anyString(), anyString())).willReturn(accessTokenResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        post("/auth/refresh")
                                .header("Authorization", "Bearer " + accessTokenRequest)
                                .cookie(new Cookie("refresh-token", "refreshToken")))
                .andExpect(status().isCreated());

        // then
        resultActions.andDo(document("oauth-access-token-refresh",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Auth")
                                        .summary("accessToken 갱신")
                                        .description("accessToken이 만료되었을 때 새로 갱신할 수 있다.")
                                        .requestHeaders(
                                                headerWithName("Authorization")
                                                        .type(SimpleType.STRING)
                                                        .description("Register Token")
                                        )
                                        .responseFields(
                                                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                                                        .description("AccessToken")
                                        )
                                        .build()
                        )
                )
        );
    }
}
