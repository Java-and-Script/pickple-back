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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class MemberDocumentTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("회원 프로필 조회")
    void findMemberById() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(get("/members/{memberId}", 1L))
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
                                                fieldWithPath("introduction").type(JsonFieldType.STRING).description("자기소개"),
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
                                        ).build()
                        )
                )
        );
    }
}
