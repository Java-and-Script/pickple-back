package kr.pickple.back.member.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.member.IntegrationMemberTest;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.request.MemberCreateRequest;

@Transactional
class MemberDocumentTest extends IntegrationMemberTest {

    @Test
    @DisplayName("회원 생성")
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
                                                headerWithName("Authorization").type(SimpleType.STRING)
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
        crewSetup.save(member);

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
                                        .description("회원 프로필을 조회한다.")
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
                                                fieldWithPath("positions[]").type(JsonFieldType.ARRAY).description("주 포지션 목록"),
                                                fieldWithPath("crews[]").type(JsonFieldType.ARRAY)
                                                        .description("회원이 소속된 크루 목록"),
                                                fieldWithPath("crews[].id").type(JsonFieldType.NUMBER).description("크루 ID"),
                                                fieldWithPath("crews[].name").type(JsonFieldType.STRING).description("크루 명"),
                                                fieldWithPath("crews[].content").type(JsonFieldType.STRING)
                                                        .description("크루 소개글"),
                                                fieldWithPath("crews[].memberCount").type(JsonFieldType.NUMBER)
                                                        .description("멤버 수"),
                                                fieldWithPath("crews[].maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("최대 인원 수"),
                                                fieldWithPath("crews[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 프로필 이미지"),
                                                fieldWithPath("crews[].backgroundImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 배경 이미지"),
                                                fieldWithPath("crews[].status").type(JsonFieldType.STRING)
                                                        .description("크루 모집 상태(모집 중, 모집 마감"),
                                                fieldWithPath("crews[].likeCount").type(JsonFieldType.NUMBER)
                                                        .description("좋아요 수"),
                                                fieldWithPath("crews[].competitionPoint").type(JsonFieldType.NUMBER)
                                                        .description("경쟁 점수"),
                                                fieldWithPath("crews[].leader.id").type(JsonFieldType.NUMBER)
                                                        .description("크루장 ID"),
                                                fieldWithPath("crews[].leader.nickname").type(JsonFieldType.STRING)
                                                        .description("크루장 닉네임"),
                                                fieldWithPath("crews[].leader.email").type(JsonFieldType.STRING)
                                                        .description("크루장 이메일"),
                                                fieldWithPath("crews[].leader.introduction").type(JsonFieldType.VARIES)
                                                        .description("크루장 소개"),
                                                fieldWithPath("crews[].leader.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루장 프로필 이미지"),
                                                fieldWithPath("crews[].leader.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("크루장 매너 점수"),
                                                fieldWithPath("crews[].leader.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("크루장 매너 스코어 반영 횟수"),
                                                fieldWithPath("crews[].leader.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루장 주 활동지역 주소1"),
                                                fieldWithPath("crews[].leader.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루장 주 활동지역 주소2"),
                                                subsectionWithPath("crews[].leader.positions").type(JsonFieldType.ARRAY)
                                                        .description("크루장 주 포지션 목록"),
                                                fieldWithPath("crews[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소1"),
                                                fieldWithPath("crews[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소2")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("회원이 가입한 크루 목록 조회")
    void findAllCrewsByMemberId_ReturnCrewProfileResponses() throws Exception {
        // given
        final Member member = memberSetup.save();
        crewSetup.save(member);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(member.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}/crews", member.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                        .queryParam("status", CONFIRMED.getDescription())
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
                                        .description("회원이 가입한 크루 목록을 조회한다.")
                                        .responseSchema(schema("CrewProfile"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
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
                                                subsectionWithPath("[].leader.positions[]").type(JsonFieldType.ARRAY)
                                                        .description("크루장 주 포지션 목록"),
                                                fieldWithPath("[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소1"),
                                                fieldWithPath("[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소2"),
                                                fieldWithPath("[].members[]").type(JsonFieldType.ARRAY)
                                                        .description("크루원 목록"),
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
                                                subsectionWithPath("[].members[].positions[]").type(JsonFieldType.ARRAY)
                                                        .description("크루원 주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("회원이 만든 크루 목록 조회")
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
                                        .description("회원이 만든 크루 목록을 조회한다.")
                                        .responseSchema(schema("CrewProfile"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
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
                                                subsectionWithPath("[].leader.positions[]").type(JsonFieldType.ARRAY)
                                                        .description("크루장 주 포지션 목록"),
                                                fieldWithPath("[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소1"),
                                                fieldWithPath("[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루 주 활동지역 주소2"),
                                                fieldWithPath("[].members[]").type(JsonFieldType.ARRAY)
                                                        .description("크루원 목록"),
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
                                                subsectionWithPath("[].members[].positions[]").type(JsonFieldType.ARRAY)
                                                        .description("크루원 주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("회원이 참여 확정된 게스트 모집글 목록 조회")
    void findAllMemberGames_ReturnGameResponses() throws Exception {
        // given
        final Member host = memberSetup.save();
        gameSetup.save(host);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(host.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}/games", host.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                        .queryParam("status", CONFIRMED.getDescription())
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-all-member-games",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Member")
                                        .summary("회원이 참여 확정된 게스트 모집글 목록 조회")
                                        .description("회원이 참여 확정된 게스트 모집글 목록을 조회한다.")
                                        .responseSchema(schema("GameResponse"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .pathParameters(
                                                parameterWithName("memberId").description("회원 ID")
                                        )
                                        .queryParameters(
                                                parameterWithName("status").description("참여 상태")
                                        )
                                        .responseFields(
                                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("게스트 모집글 ID"),
                                                fieldWithPath("[].content").type(JsonFieldType.STRING)
                                                        .description("게스트 모집글 내용"),
                                                fieldWithPath("[].playDate").type(JsonFieldType.STRING).description("경기 날짜"),
                                                fieldWithPath("[].playStartTime").type(JsonFieldType.STRING)
                                                        .description("경기 시작 시간"),
                                                fieldWithPath("[].playEndTime").type(JsonFieldType.STRING)
                                                        .description("경기 종료 시간"),
                                                fieldWithPath("[].playTimeMinutes").type(JsonFieldType.NUMBER)
                                                        .description("경기 진행 분"),
                                                fieldWithPath("[].mainAddress").type(JsonFieldType.STRING)
                                                        .description("메인 주소(도/시, 구, 동, 번지)"),
                                                fieldWithPath("[].detailAddress").type(JsonFieldType.STRING)
                                                        .description("상세 주소(층, 호수)"),
                                                fieldWithPath("[].latitude").type(JsonFieldType.VARIES).description("위도"),
                                                fieldWithPath("[].longitude").type(JsonFieldType.VARIES).description("경도"),
                                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("게스트 모집 상태"),
                                                fieldWithPath("[].isReviewDone").type(JsonFieldType.BOOLEAN).description("리뷰 완료 여부"),
                                                fieldWithPath("[].viewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                                fieldWithPath("[].cost").type(JsonFieldType.NUMBER).description("비용"),
                                                fieldWithPath("[].memberCount").type(JsonFieldType.NUMBER).description("인원 수"),
                                                fieldWithPath("[].maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("인원 제한"),
                                                fieldWithPath("[].host").type(JsonFieldType.OBJECT).description("호스트 정보"),
                                                fieldWithPath("[].host.id").type(JsonFieldType.NUMBER)
                                                        .description("호스트.사용자 ID"),
                                                fieldWithPath("[].host.email").type(JsonFieldType.STRING)
                                                        .description("호스트.이메일"),
                                                fieldWithPath("[].host.nickname").type(JsonFieldType.STRING)
                                                        .description("호스트.닉네임"),
                                                fieldWithPath("[].host.introduction").type(JsonFieldType.VARIES)
                                                        .description("호스트.자기소개"),
                                                fieldWithPath("[].host.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("호스트.프로필 이미지 경로"),
                                                fieldWithPath("[].host.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("호스트.매너 스코어"),
                                                fieldWithPath("[].host.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("호스트.매너 스코어 반영 횟수"),
                                                fieldWithPath("[].host.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("호스트.주소1(도,시)"),
                                                fieldWithPath("[].host.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("호스트.주소2(구)"),
                                                fieldWithPath("[].host.positions").type(JsonFieldType.ARRAY)
                                                        .description("호스트.포지션 목록"),
                                                fieldWithPath("[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("주소2(구)"),
                                                fieldWithPath("[].positions").type(JsonFieldType.ARRAY).description("포지션 목록"),
                                                fieldWithPath("[].members[]").type(JsonFieldType.ARRAY)
                                                        .description("게스트 모집글에 참여 신청한 사용자 목록"),
                                                fieldWithPath("[].members[].id").type(JsonFieldType.NUMBER)
                                                        .description("회원 ID"),
                                                fieldWithPath("[].members[].email").type(JsonFieldType.STRING)
                                                        .description("이메일"),
                                                fieldWithPath("[].members[].nickname").type(JsonFieldType.STRING)
                                                        .description("닉네임"),
                                                fieldWithPath("[].members[].introduction").type(JsonFieldType.VARIES)
                                                        .description("자기 소개"),
                                                fieldWithPath("[].members[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("[].members[].mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어"),
                                                fieldWithPath("[].members[].mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어 반영 개수"),
                                                fieldWithPath("[].members[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("[].members[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("주소2(구)"),
                                                fieldWithPath("[].members[].positions").type(JsonFieldType.ARRAY)
                                                        .description("주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("회원이 만든 게스트 모집글 목록 조회")
    void findAllCreatedGames_ReturnGameResponses() throws Exception {
        // given
        final Member host = memberSetup.save();
        gameSetup.save(host);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(host.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}/created-games", host.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-all-created-games",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Member")
                                        .summary("회원이 만든 게스트 모집글 목록 조회")
                                        .description("회원이 만든 게스트 모집글 목록을 조회한다.")
                                        .responseSchema(schema("GameResponse"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .pathParameters(
                                                parameterWithName("memberId").description("회원 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("게스트 모집글 ID"),
                                                fieldWithPath("[].content").type(JsonFieldType.STRING)
                                                        .description("게스트 모집글 내용"),
                                                fieldWithPath("[].playDate").type(JsonFieldType.STRING).description("경기 날짜"),
                                                fieldWithPath("[].playStartTime").type(JsonFieldType.STRING)
                                                        .description("경기 시작 시간"),
                                                fieldWithPath("[].playEndTime").type(JsonFieldType.STRING)
                                                        .description("경기 종료 시간"),
                                                fieldWithPath("[].playTimeMinutes").type(JsonFieldType.NUMBER)
                                                        .description("경기 진행 분"),
                                                fieldWithPath("[].mainAddress").type(JsonFieldType.STRING)
                                                        .description("메인 주소(도/시, 구, 동, 번지)"),
                                                fieldWithPath("[].detailAddress").type(JsonFieldType.STRING)
                                                        .description("상세 주소(층, 호수)"),
                                                fieldWithPath("[].latitude").type(JsonFieldType.VARIES).description("위도"),
                                                fieldWithPath("[].longitude").type(JsonFieldType.VARIES).description("경도"),
                                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("게스트 모집 상태"),
                                                fieldWithPath("[].isReviewDone").type(JsonFieldType.BOOLEAN).description("리뷰 완료 여부"),
                                                fieldWithPath("[].viewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                                fieldWithPath("[].cost").type(JsonFieldType.NUMBER).description("비용"),
                                                fieldWithPath("[].memberCount").type(JsonFieldType.NUMBER).description("인원 수"),
                                                fieldWithPath("[].maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("인원 제한"),
                                                fieldWithPath("[].host").type(JsonFieldType.OBJECT).description("호스트 정보"),
                                                fieldWithPath("[].host.id").type(JsonFieldType.NUMBER)
                                                        .description("호스트.사용자 ID"),
                                                fieldWithPath("[].host.email").type(JsonFieldType.STRING)
                                                        .description("호스트.이메일"),
                                                fieldWithPath("[].host.nickname").type(JsonFieldType.STRING)
                                                        .description("호스트.닉네임"),
                                                fieldWithPath("[].host.introduction").type(JsonFieldType.VARIES)
                                                        .description("호스트.자기소개"),
                                                fieldWithPath("[].host.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("호스트.프로필 이미지 경로"),
                                                fieldWithPath("[].host.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("호스트.매너 스코어"),
                                                fieldWithPath("[].host.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("호스트.매너 스코어 반영 횟수"),
                                                fieldWithPath("[].host.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("호스트.주소1(도,시)"),
                                                fieldWithPath("[].host.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("호스트.주소2(구)"),
                                                fieldWithPath("[].host.positions").type(JsonFieldType.ARRAY)
                                                        .description("호스트.포지션 목록"),
                                                fieldWithPath("[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("주소2(구)"),
                                                fieldWithPath("[].positions").type(JsonFieldType.ARRAY).description("포지션 목록"),
                                                fieldWithPath("[].members[]").type(JsonFieldType.ARRAY)
                                                        .description("게스트 모집글에 참여 신청한 사용자 목록"),
                                                fieldWithPath("[].members[].id").type(JsonFieldType.NUMBER)
                                                        .description("회원 ID"),
                                                fieldWithPath("[].members[].email").type(JsonFieldType.STRING)
                                                        .description("이메일"),
                                                fieldWithPath("[].members[].nickname").type(JsonFieldType.STRING)
                                                        .description("닉네임"),
                                                fieldWithPath("[].members[].introduction").type(JsonFieldType.VARIES)
                                                        .description("자기 소개"),
                                                fieldWithPath("[].members[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("[].members[].mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어"),
                                                fieldWithPath("[].members[].mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어 반영 개수"),
                                                fieldWithPath("[].members[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("[].members[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("주소2(구)"),
                                                fieldWithPath("[].members[].positions").type(JsonFieldType.ARRAY)
                                                        .description("주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        );
    }
}
