package kr.pickple.back.crew.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.crew.IntegrationCrewTest;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.dto.request.CrewMemberUpdateStatusRequest;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.fixture.dto.CrewDtoFixtures;
import kr.pickple.back.fixture.setup.AddressSetup;
import kr.pickple.back.member.domain.Member;

@Transactional
public class CrewEntityDocumentTest extends IntegrationCrewTest {

    private static final String BASE_URL = "/crews";

    @Autowired
    private AddressSetup addressSetup;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Test
    @DisplayName("크루원 모집글 상세 정보 조회")
    void findCrewById_ReturnCrewResponse() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithWaitingMembers(3);

        //when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{crewId}", crew.getId()))
                .andExpect(status().isOk());

        //then
        resultActions.andDo(document("find-crew",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Crew")
                                        .summary("크루원 모집글 상세 조회")
                                        .description("크루원 모집글 정보를 상세 조회한다")
                                        .responseSchema(schema("CrewResponse"))
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루원 모집글 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("크루원 모집글 ID"),
                                                fieldWithPath("name").type(JsonFieldType.STRING)
                                                        .description("크루 이름"),
                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                        .description("크루원 모집글 내용"),
                                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER)
                                                        .description("해당 크루의 크루원 수"),
                                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 프로필 이미지"),
                                                fieldWithPath("backgroundImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 배경 이미지"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("크루원 모집 상태"),
                                                fieldWithPath("likeCount").type(JsonFieldType.NUMBER)
                                                        .description("좋아요 수"),
                                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER)
                                                        .description("인원 수"),
                                                fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("인원 제한"),
                                                fieldWithPath("competitionPoint").type(JsonFieldType.NUMBER)
                                                        .description("경쟁 점수"),
                                                fieldWithPath("leader").type(JsonFieldType.OBJECT).description("크루장 정보"),
                                                fieldWithPath("leader.id").type(JsonFieldType.NUMBER).description("크루장.사용자 ID"),
                                                fieldWithPath("leader.email").type(JsonFieldType.STRING).description("크루장.이메일"),
                                                fieldWithPath("leader.nickname").type(JsonFieldType.STRING)
                                                        .description("크루장.닉네임"),
                                                fieldWithPath("leader.introduction").type(JsonFieldType.VARIES)
                                                        .description("크루장.자기소개"),
                                                fieldWithPath("leader.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루장.프로필 이미지 경로"),
                                                fieldWithPath("leader.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("크루장.매너 스코어"),
                                                fieldWithPath("leader.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("크루장.매너 스코어 반영 횟수"),
                                                fieldWithPath("leader.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루장.주소1(도,시)"),
                                                fieldWithPath("leader.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루장.주소2(구)"),
                                                fieldWithPath("leader.positions").type(JsonFieldType.ARRAY)
                                                        .description("크루장.포지션 목록"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.STRING).description("주소2(구)"),
                                                fieldWithPath("members[]").type(JsonFieldType.ARRAY)
                                                        .description("크루원 모집글에 참여 확정된 사용자 목록"),
                                                fieldWithPath("members[].id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                                fieldWithPath("members[].email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("members[].nickname").type(JsonFieldType.STRING)
                                                        .description("닉네임"),
                                                fieldWithPath("members[].introduction").type(JsonFieldType.VARIES)
                                                        .description("자기 소개"),
                                                fieldWithPath("members[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("members[].mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어"),
                                                fieldWithPath("members[].mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어 반영 개수"),
                                                fieldWithPath("members[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("members[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("주소2(구)"),
                                                fieldWithPath("members[].positions").type(JsonFieldType.ARRAY)
                                                        .description("주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("크루 가입 신청")
    void applyForCrewMemberShip_ReturnVoid() throws Exception {
        //given
        final List<Member> members = memberSetup.save(2);
        final Member crewLeader = members.get(0);
        final Member crewApplyMember = members.get(1);
        final CrewEntity crew = crewSetup.save(crewLeader);

        final String subject = String.valueOf(crewApplyMember.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        //when
        final ResultActions resultActions = mockMvc.perform(
                        post(BASE_URL + "/{crewId}/members", crew.getId())
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isNoContent());

        //then
        resultActions.andDo(document("register-crewMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Crew")
                                        .summary("크루원 모집 참여 신청")
                                        .description("크루원 모집에 참여 신청을 한다.")
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루원 모집글 ID")
                                        )
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("크루원 모집에 참여 신청된 혹은 확정된 사용자 정보 목록 조회")
    void findAllCrewMembers_ReturnCrewResponseWithWaitingMembers() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithConfirmedMembers(2);
        final Member crewLeader = crew.getLeader();

        final String subject = String.valueOf(crewLeader.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        //when
        final ResultActions resultActions = mockMvc.perform(
                        get(BASE_URL + "/{crewId}/members", crew.getId())
                                .param("status", CONFIRMED.getDescription())
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isOk());

        //then
        resultActions.andDo(document("find-crew",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Crew")
                                        .summary("크루원 모집글 상세 조회")
                                        .description("크루원 모집글 정보를 상세 조회한다")
                                        .responseSchema(schema("CrewResponse"))
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루원 모집글 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("크루원 모집글 ID"),
                                                fieldWithPath("name").type(JsonFieldType.STRING)
                                                        .description("크루 이름"),
                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                        .description("크루원 모집글 내용"),
                                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER)
                                                        .description("해당 크루의 크루원 수"),
                                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 프로필 이미지"),
                                                fieldWithPath("backgroundImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 배경 이미지"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("크루원 모집 상태"),
                                                fieldWithPath("likeCount").type(JsonFieldType.NUMBER)
                                                        .description("좋아요 수"),
                                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER)
                                                        .description("인원 수"),
                                                fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("인원 제한"),
                                                fieldWithPath("competitionPoint").type(JsonFieldType.NUMBER)
                                                        .description("경쟁 점수"),
                                                fieldWithPath("leader").type(JsonFieldType.OBJECT).description("크루장 정보"),
                                                fieldWithPath("leader.id").type(JsonFieldType.NUMBER).description("크루장.사용자 ID"),
                                                fieldWithPath("leader.email").type(JsonFieldType.STRING).description("크루장.이메일"),
                                                fieldWithPath("leader.nickname").type(JsonFieldType.STRING)
                                                        .description("크루장.닉네임"),
                                                fieldWithPath("leader.introduction").type(JsonFieldType.VARIES)
                                                        .description("크루장.자기소개"),
                                                fieldWithPath("leader.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루장.프로필 이미지 경로"),
                                                fieldWithPath("leader.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("크루장.매너 스코어"),
                                                fieldWithPath("leader.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("크루장.매너 스코어 반영 횟수"),
                                                fieldWithPath("leader.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루장.주소1(도,시)"),
                                                fieldWithPath("leader.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루장.주소2(구)"),
                                                fieldWithPath("leader.positions").type(JsonFieldType.ARRAY)
                                                        .description("크루장.포지션 목록"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.STRING).description("주소2(구)"),
                                                fieldWithPath("members[]").type(JsonFieldType.ARRAY)
                                                        .description("크루원 모집글에 참여 확정된 사용자 목록"),
                                                fieldWithPath("members[].id").type(JsonFieldType.NUMBER).description("회원 ID"),
                                                fieldWithPath("members[].email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("members[].nickname").type(JsonFieldType.STRING)
                                                        .description("닉네임"),
                                                fieldWithPath("members[].introduction").type(JsonFieldType.VARIES)
                                                        .description("자기 소개"),
                                                fieldWithPath("members[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로"),
                                                fieldWithPath("members[].mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어"),
                                                fieldWithPath("members[].mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("매너 스코어 반영 개수"),
                                                fieldWithPath("members[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("members[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("주소2(구)"),
                                                fieldWithPath("members[].positions").type(JsonFieldType.ARRAY)
                                                        .description("주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("크루원 모집 참여 신청 수락")
    void updateCrewMemberRegistrationStatus_ReturnVoid() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithWaitingMembers(2);
        final Member crewLeader = crew.getLeader();
        final Member crewMember = crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), WAITING)
                .get(0)
                .getMember();

        final String subject = String.valueOf(crewLeader.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        final CrewMemberUpdateStatusRequest crewMemberUpdateStatusRequest = CrewDtoFixtures
                .crewMemberUpdateStatusRequest(CONFIRMED);
        final String requestBody = objectMapper.writeValueAsString(crewMemberUpdateStatusRequest);

        //when
        final ResultActions resultActions = mockMvc.perform(
                        patch(BASE_URL + "/{crewId}/members/{memberId}", crew.getId(), crewMember.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                                .content(requestBody)
                )
                .andExpect(status().isNoContent());

        //then
        resultActions.andDo(document("confirm-crewMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Crew")
                                        .summary("크루원 모집 참여 신청 수락")
                                        .description("크루장이 크루원 모집 참여 신청을 수락 한다.")
                                        .requestSchema(schema("CrewMemberUpdateStatusRequest"))
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루원 모집글 ID"),
                                                parameterWithName("memberId").description("참여 신청한 사용자 ID")
                                        )
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .requestFields(
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description(CONFIRMED.getDescription())
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("크루원 모집 참여 신청 거절/취소")
    void deleteCrewMember_ReturnVoid() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithWaitingMembers(2);
        final Member crewLeader = crew.getLeader();
        final Member crewMember = crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), WAITING)
                .get(0)
                .getMember();

        final String subject = String.valueOf(crewLeader.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        //when
        final ResultActions resultActions = mockMvc.perform(
                        delete(BASE_URL + "/{crewId}/members/{memberId}", crew.getId(), crewMember.getId())
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isNoContent());

        //then
        resultActions.andDo(document("delete-crewMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Crew")
                                        .summary("크루원 모집 참여 신청 거절/취소")
                                        .description("크루원 모집 참여 신청에 대해 거절 혹은 취소한다.")
                                        .pathParameters(
                                                parameterWithName("crewId").description("크루원 모집글 ID"),
                                                parameterWithName("memberId").description("참여 신청한 사용자 ID")
                                        )
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("사용자 위치 근처 크루 조회")
    void findCrewsByAddress_ReturnCrews() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithConfirmedMembers(2);
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        //when
        final ResultActions resultActions = mockMvc.perform(
                        get(BASE_URL)
                                .param("addressDepth1", addressDepth1.getName())
                                .param("addressDepth2", addressDepth2.getName())
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk());

        //then
        resultActions.andDo(document("findCrewsByAddress",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Crew")
                                        .summary("사용자 위치 근처 크루 조회")
                                        .description("사용자 위치를 기반으로 근처의 크루를 조회한다.")
                                        .responseSchema(schema("CrewResponse"))
                                        .responseFields(
                                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("크루원 모집글 ID"),
                                                fieldWithPath("[].name").type(JsonFieldType.STRING)
                                                        .description("크루 이름"),
                                                fieldWithPath("[].content").type(JsonFieldType.STRING)
                                                        .description("크루원 모집글 내용"),
                                                fieldWithPath("[].memberCount").type(JsonFieldType.NUMBER)
                                                        .description("해당 크루의 크루원 수"),
                                                fieldWithPath("[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 프로필 이미지"),
                                                fieldWithPath("[].backgroundImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루 배경 이미지"),
                                                fieldWithPath("[].status").type(JsonFieldType.STRING)
                                                        .description("크루원 모집 상태"),
                                                fieldWithPath("[].likeCount").type(JsonFieldType.NUMBER)
                                                        .description("좋아요 수"),
                                                fieldWithPath("[].memberCount").type(JsonFieldType.NUMBER)
                                                        .description("인원 수"),
                                                fieldWithPath("[].maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("인원 제한"),
                                                fieldWithPath("[].competitionPoint").type(JsonFieldType.NUMBER)
                                                        .description("경쟁 점수"),
                                                fieldWithPath("[].leader").type(JsonFieldType.OBJECT)
                                                        .description("크루장 정보"),
                                                fieldWithPath("[].leader.id").type(JsonFieldType.NUMBER)
                                                        .description("크루장.사용자 ID"),
                                                fieldWithPath("[].leader.email").type(JsonFieldType.STRING)
                                                        .description("크루장.이메일"),
                                                fieldWithPath("[].leader.nickname").type(JsonFieldType.STRING)
                                                        .description("크루장.닉네임"),
                                                fieldWithPath("[].leader.introduction").type(JsonFieldType.VARIES)
                                                        .description("크루장.자기소개"),
                                                fieldWithPath("[].leader.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("크루장.프로필 이미지 경로"),
                                                fieldWithPath("[].leader.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("크루장.매너 스코어"),
                                                fieldWithPath("[].leader.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("크루장.매너 스코어 반영 횟수"),
                                                fieldWithPath("[].leader.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("크루장.주소1(도,시)"),
                                                fieldWithPath("[].leader.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("크루장.주소2(구)"),
                                                fieldWithPath("[].leader.positions").type(JsonFieldType.ARRAY)
                                                        .description("크루장.포지션 목록"),
                                                fieldWithPath("[].addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("[].addressDepth2").type(JsonFieldType.STRING)
                                                        .description("주소2(구)"),
                                                fieldWithPath("[].members").type(JsonFieldType.ARRAY)
                                                        .description("크루원 모집글에 참여 확정된 사용자 목록"),
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
                                                        .description("주 포지션 목록"),
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
