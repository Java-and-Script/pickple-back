package kr.pickple.back.member.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.fixture.setup.MemberSetup;
import kr.pickple.back.member.dto.request.MemberCreateRequest;

@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class MemberDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberSetup memberSetup;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("회원을 생성할 수 있다.")
    void createMember_ReturnAuthenticatedMemberResponse() throws Exception {
        // given
        final MemberCreateRequest memberCreateRequest = MemberDtoFixtures.memberCreateRequestBuild();
        final String requestBody = objectMapper.writeValueAsString(memberCreateRequest);

        final String subject = memberCreateRequest.getOauthProvider().toString() + memberCreateRequest.getOauthId();
        final AuthTokens authTokens = jwtProvider.createRegisterToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Authorization", "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isCreated());

        // then
        resultActions.andDo(document("create-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Member")
                                        .summary("회원 생성")
                                        .description("회원을 생성한다.")
                                        .requestSchema(schema("MemberCreateRequest"))
                                        .responseSchema(schema("MemberProfileResponse"))
                                        .requestHeaders(
                                                headerWithName("Authorization")
                                                        .description("Register Token"))
                                        .requestFields(
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("positions").type(JsonFieldType.ARRAY).description("주 포지션 목록"),
                                                fieldWithPath("oauthId").type(JsonFieldType.NUMBER).description("oauthId"),
                                                fieldWithPath("oauthProvider").type(JsonFieldType.STRING)
                                                        .description("oauth 제공자"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.STRING).description("주소2(구)")
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
        );
    }

    @Test
    @DisplayName("회원 프로필 조회")
    void findMemberById_ReturnMemberProfileResponse() throws Exception {
        // given
        memberSetup.save();

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}", 1L))
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Member")
                                        .summary("회원 프로필 조회")
                                        .description("회원 프로필을 조회한다")
                                        .responseSchema(schema("MemberProfileResponse"))
                                        .pathParameters(
                                                parameterWithName("memberId").description("회원 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                                fieldWithPath("introduction").type(JsonFieldType.VARIES).description("자기 소개"),
                                                //TODO: 추후 introduction 저장시 null이 아닌 빈 값 저장 필요 (11.4 황창현)
                                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("mannerScore").type(JsonFieldType.NUMBER).description("매너 스코어"),
                                                fieldWithPath("mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어 반영 개수"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.STRING).description("주소2(구)"),
                                                fieldWithPath("positions").type(JsonFieldType.ARRAY).description("주 포지션 목록"),
                                                fieldWithPath("crews").type(JsonFieldType.NULL).description("사용자가 소속된 크루 목록")
                                                //TODO: 추후 Crew 도메인 완성 시, 해당 필드에 대한 로직 추가 예정 (11.4 황창현)
                                        )
                                        .build()
                        )
                )
        );
    }
}
