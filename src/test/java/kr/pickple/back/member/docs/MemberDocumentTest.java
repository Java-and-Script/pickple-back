package kr.pickple.back.member.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static org.springframework.http.HttpHeaders.*;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.fixture.setup.CrewSetup;
import kr.pickple.back.fixture.setup.MemberSetup;
import kr.pickple.back.member.domain.Member;
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
    private CrewSetup crewSetup;

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
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
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
                                                        .type(SimpleType.STRING)
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
        final Member member = memberSetup.save();

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}", member.getId()))
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
                                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("mannerScore").type(JsonFieldType.NUMBER).description("매너 스코어"),
                                                fieldWithPath("mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어 반영 개수"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.STRING).description("주소2(구)"),
                                                fieldWithPath("positions").type(JsonFieldType.ARRAY).description("주 포지션 목록"),
                                                fieldWithPath("crews").type(JsonFieldType.ARRAY).description("사용자가 소속된 크루 목록")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("회원이 가입한 크루 목록을 조회할 수 있다.")
    void findAllCrewsByMemberId_ReturnCrewProfileResponses() throws Exception {
        // given
        final Member member = memberSetup.save();
        crewSetup.save(member);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(member.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}/crews", member.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                        .queryParam("status", "확정")
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-all-crews-by-member-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Member")
                                        .summary("회원이 가입한 크루 목록 조회")
                                        .description("회원이 가입한 크루 목록을 조회 할 수 있다.")
                                        .responseSchema(schema("CrewProfile"))
                                        .requestHeaders(
                                                headerWithName("Authorization").description("AccessToken")
                                        )
                                        .pathParameters(
                                                parameterWithName("memberId").description("회원 ID")
                                        )
                                        .queryParameters(
                                                parameterWithName("status").description("가입 상태")
                                        )
                                        .responseFields(
                                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("크루 ID"),
                                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("크루 명"),
                                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("크루 소개글"),
                                                fieldWithPath("[].memberCount").type(JsonFieldType.NUMBER).description("멤버 수"),
                                                fieldWithPath("[].maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("최대 인원 수"),
                                                fieldWithPath("[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 프로필 이미지"),
                                                fieldWithPath("[].backgroundImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 배경 이미지"),
                                                fieldWithPath("[].status").type(JsonFieldType.STRING)
                                                        .description("크루 모집 상태(모집 중, 모집 마감"),
                                                fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                fieldWithPath("[].competitionPoint").type(JsonFieldType.NUMBER)
                                                        .description("경쟁 점수"),
                                                fieldWithPath("[].leader.id").type(JsonFieldType.NUMBER).description("크루장 ID"),
                                                fieldWithPath("[].leader.nickname").type(JsonFieldType.STRING)
                                                        .description("크루장 닉네임"),
                                                fieldWithPath("[].leader.email").type(JsonFieldType.STRING)
                                                        .description("크루장 이메일"),
                                                fieldWithPath("[].leader.introduction").type(JsonFieldType.VARIES)
                                                        .description("크루장 소개"),
                                                fieldWithPath("[].leader.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루장 프로필 이미지"),
                                                fieldWithPath("[].leader.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("크루장 매너 점수"),
                                                fieldWithPath("[].leader.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("크루장 매너 스코어 반영 횟수"),
                                                fieldWithPath("[].leader.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루장 주 활동지역 주소1"),
                                                fieldWithPath("[].leader.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루장 주 활동지역 주소2"),
                                                subsectionWithPath("[].leader.positions").type(JsonFieldType.ARRAY)
                                                        .description("크루장 주 포지션 목록"),
                                                fieldWithPath("[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소1"),
                                                fieldWithPath("[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소2"),
                                                fieldWithPath("[].members[].id").type(JsonFieldType.NUMBER)
                                                        .description("크루원 ID"),
                                                fieldWithPath("[].members[].nickname").type(JsonFieldType.STRING)
                                                        .description("크루원 닉네임"),
                                                fieldWithPath("[].members[].email").type(JsonFieldType.STRING)
                                                        .description("크루원 이메일"),
                                                fieldWithPath("[].members[].introduction").type(JsonFieldType.VARIES)
                                                        .description("크루원 소개"),
                                                fieldWithPath("[].members[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루원 프로필 이미지"),
                                                fieldWithPath("[].members[].mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("크루원 매너 점수"),
                                                fieldWithPath("[].members[].mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("크루원 매너 스코어 반영 횟수"),
                                                fieldWithPath("[].members[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루원 주 활동지역 주소1"),
                                                fieldWithPath("[].members[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루원 주 활동지역 주소2"),
                                                subsectionWithPath("[].members[].positions").type(JsonFieldType.ARRAY)
                                                        .description("크루원 주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        ).andDo(print());
    }

    @Test
    @DisplayName("회원이 만든 크루 목록을 조회할 수 있다.")
    void findCreatedCrewsByMemberId_ReturnCrewProfileResponses() throws Exception {
        // given
        final Member member = memberSetup.save();
        crewSetup.save(member);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(member.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}/created-crews", member.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-created-crews-by-member-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Member")
                                        .summary("회원이 만든 크루 목록 조회")
                                        .description("회원이 만든 크루 목록을 조회 할 수 있다.")
                                        .responseSchema(schema("CrewProfile"))
                                        .requestHeaders(
                                                headerWithName("Authorization").description("AccessToken")
                                        )
                                        .pathParameters(
                                                parameterWithName("memberId").description("회원 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("크루 ID"),
                                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("크루 명"),
                                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("크루 소개글"),
                                                fieldWithPath("[].memberCount").type(JsonFieldType.NUMBER).description("멤버 수"),
                                                fieldWithPath("[].maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("최대 인원 수"),
                                                fieldWithPath("[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 프로필 이미지"),
                                                fieldWithPath("[].backgroundImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 배경 이미지"),
                                                fieldWithPath("[].status").type(JsonFieldType.STRING)
                                                        .description("크루 모집 상태(모집 중, 모집 마감"),
                                                fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                                                fieldWithPath("[].competitionPoint").type(JsonFieldType.NUMBER)
                                                        .description("경쟁 점수"),
                                                fieldWithPath("[].leader.id").type(JsonFieldType.NUMBER).description("크루장 ID"),
                                                fieldWithPath("[].leader.nickname").type(JsonFieldType.STRING)
                                                        .description("크루장 닉네임"),
                                                fieldWithPath("[].leader.email").type(JsonFieldType.STRING)
                                                        .description("크루장 이메일"),
                                                fieldWithPath("[].leader.introduction").type(JsonFieldType.VARIES)
                                                        .description("크루장 소개"),
                                                fieldWithPath("[].leader.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루장 프로필 이미지"),
                                                fieldWithPath("[].leader.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("크루장 매너 점수"),
                                                fieldWithPath("[].leader.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("크루장 매너 스코어 반영 횟수"),
                                                fieldWithPath("[].leader.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루장 주 활동지역 주소1"),
                                                fieldWithPath("[].leader.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루장 주 활동지역 주소2"),
                                                subsectionWithPath("[].leader.positions").type(JsonFieldType.ARRAY)
                                                        .description("크루장 주 포지션 목록"),
                                                fieldWithPath("[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소1"),
                                                fieldWithPath("[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소2"),
                                                fieldWithPath("[].members[].id").type(JsonFieldType.NUMBER)
                                                        .description("크루원 ID"),
                                                fieldWithPath("[].members[].nickname").type(JsonFieldType.STRING)
                                                        .description("크루원 닉네임"),
                                                fieldWithPath("[].members[].email").type(JsonFieldType.STRING)
                                                        .description("크루원 이메일"),
                                                fieldWithPath("[].members[].introduction").type(JsonFieldType.VARIES)
                                                        .description("크루원 소개"),
                                                fieldWithPath("[].members[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루원 프로필 이미지"),
                                                fieldWithPath("[].members[].mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("크루원 매너 점수"),
                                                fieldWithPath("[].members[].mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("크루원 매너 스코어 반영 횟수"),
                                                fieldWithPath("[].members[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루원 주 활동지역 주소1"),
                                                fieldWithPath("[].members[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루원 주 활동지역 주소2"),
                                                subsectionWithPath("[].members[].positions").type(JsonFieldType.ARRAY)
                                                        .description("크루원 주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        ).andDo(print());
    }
}
