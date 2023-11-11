package kr.pickple.back.game.docs;

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
import kr.pickple.back.fixture.dto.GameDtoFixtures;
import kr.pickple.back.fixture.setup.GameSetup;
import kr.pickple.back.fixture.setup.MemberSetup;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.request.MannerScoreReviewsRequest;
import kr.pickple.back.member.domain.Member;

@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class GameDocumentTest {

    private static final String BASE_URL = "/games";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberSetup memberSetup;

    @Autowired
    private GameSetup gameSetup;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게스트 모집글 상세 조회")
    void findGameById_ReturnGameResponse() throws Exception {
        // given
        final Game game = gameSetup.saveWithConfirmedMembers(3);

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{gameId}", game.getId()))
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-game",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game")
                                        .summary("게스트 모집글 상세 조회")
                                        .description("게스트 모집글 정보를 상세 조회한다")
                                        .responseSchema(schema("GameResponse"))
                                        .pathParameters(
                                                parameterWithName("gameId").description("게스트 모집글 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게스트 모집글 ID"),
                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                        .description("게스트 모집글 내용"),
                                                fieldWithPath("playDate").type(JsonFieldType.STRING).description("경기 날짜"),
                                                fieldWithPath("playStartTime").type(JsonFieldType.STRING)
                                                        .description("경기 시작 시간"),
                                                fieldWithPath("playEndTime").type(JsonFieldType.STRING).description("경기 종료 시간"),
                                                fieldWithPath("playTimeMinutes").type(JsonFieldType.NUMBER)
                                                        .description("경기 진행 분"),
                                                fieldWithPath("mainAddress").type(JsonFieldType.STRING)
                                                        .description("메인 주소(도/시, 구, 동, 번지)"),
                                                fieldWithPath("detailAddress").type(JsonFieldType.STRING)
                                                        .description("상세 주소(층, 호수)"),
                                                fieldWithPath("latitude").type(JsonFieldType.VARIES).description("위도"),
                                                fieldWithPath("longitude").type(JsonFieldType.VARIES).description("경도"),
                                                fieldWithPath("status").type(JsonFieldType.STRING).description("게스트 모집 상태"),
                                                fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                                fieldWithPath("cost").type(JsonFieldType.NUMBER).description("비용"),
                                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER).description("인원 수"),
                                                fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER).description("인원 제한"),
                                                fieldWithPath("host").type(JsonFieldType.OBJECT).description("호스트 정보"),
                                                fieldWithPath("host.id").type(JsonFieldType.NUMBER).description("호스트.사용자 ID"),
                                                fieldWithPath("host.email").type(JsonFieldType.STRING).description("호스트.이메일"),
                                                fieldWithPath("host.nickname").type(JsonFieldType.STRING)
                                                        .description("호스트.닉네임"),
                                                fieldWithPath("host.introduction").type(JsonFieldType.VARIES)
                                                        .description("호스트.자기소개"),
                                                fieldWithPath("host.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("호스트.프로필 이미지 경로"),
                                                fieldWithPath("host.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("호스트.매너 스코어"),
                                                fieldWithPath("host.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("호스트.매너 스코어 반영 횟수"),
                                                fieldWithPath("host.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("호스트.주소1(도,시)"),
                                                fieldWithPath("host.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("호스트.주소2(구)"),
                                                fieldWithPath("host.positions").type(JsonFieldType.ARRAY)
                                                        .description("호스트.포지션 목록"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.STRING).description("주소2(구)"),
                                                fieldWithPath("positions").type(JsonFieldType.ARRAY).description("포지션 목록"),
                                                fieldWithPath("members[]").type(JsonFieldType.ARRAY)
                                                        .description("게스트 모집글에 참여 확정된 사용자 목록"),
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
    @DisplayName("게스트 모집 참여 신청")
    void registerGameMember_ReturnVoid() throws Exception {
        // given
        final List<Member> members = memberSetup.save(2);
        final Member host = members.get(0);
        final Member guest = members.get(1);
        final Game game = gameSetup.save(host);

        final String subject = String.valueOf(guest.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        post(BASE_URL + "/{gameId}/members", game.getId())
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isNoContent());

        // then
        resultActions.andDo(document("register-gameMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game")
                                        .summary("게스트 모집 참여 신청")
                                        .description("게스트 모집에 참여 신청을 한다.")
                                        .pathParameters(
                                                parameterWithName("gameId").description("게스트 모집글 ID")
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
    @DisplayName("게스트 모집에 참여 신청된 혹은 확정된 사용자 정보 목록 조회")
    void findAllGameMembers_ReturnGameResponseWithWaitingMembers() throws Exception {
        // given
        final Game game = gameSetup.saveWithWaitingMembers(3);
        final Member host = game.getHost();

        final String subject = String.valueOf(host.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        get(BASE_URL + "/{gameId}/members", game.getId())
                                .param("status", WAITING.getDescription())
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken()))
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-all-waiting-or-confirmed-gameMembers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game")
                                        .summary("게스트 모집에 참여 신청된 혹은 확정된 사용자 정보 목록 조회")
                                        .description("게스트 모집에 참여 신청된 혹은 확정된 사용자 정보 목록을 조회한다.")
                                        .responseSchema(schema("GameResponse"))
                                        .pathParameters(
                                                parameterWithName("gameId").description("게스트 모집글 ID")
                                        )
                                        .queryParameters(
                                                parameterWithName("status").description("사용자의 참여 상태 (대기 혹은 확정)")
                                        )
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게스트 모집글 ID"),
                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                        .description("게스트 모집글 내용"),
                                                fieldWithPath("playDate").type(JsonFieldType.STRING).description("경기 날짜"),
                                                fieldWithPath("playStartTime").type(JsonFieldType.STRING)
                                                        .description("경기 시작 시간"),
                                                fieldWithPath("playEndTime").type(JsonFieldType.STRING).description("경기 종료 시간"),
                                                fieldWithPath("playTimeMinutes").type(JsonFieldType.NUMBER)
                                                        .description("경기 진행 분"),
                                                fieldWithPath("mainAddress").type(JsonFieldType.STRING)
                                                        .description("메인 주소(도/시, 구, 동, 번지)"),
                                                fieldWithPath("detailAddress").type(JsonFieldType.STRING)
                                                        .description("상세 주소(층, 호수)"),
                                                fieldWithPath("latitude").type(JsonFieldType.VARIES).description("위도"),
                                                fieldWithPath("longitude").type(JsonFieldType.VARIES).description("경도"),
                                                fieldWithPath("status").type(JsonFieldType.STRING).description("게스트 모집 상태"),
                                                fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                                                fieldWithPath("cost").type(JsonFieldType.NUMBER).description("비용"),
                                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER).description("인원 수"),
                                                fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER).description("인원 제한"),
                                                fieldWithPath("host").type(JsonFieldType.OBJECT).description("호스트 정보"),
                                                fieldWithPath("host.id").type(JsonFieldType.NUMBER).description("호스트.사용자 ID"),
                                                fieldWithPath("host.email").type(JsonFieldType.STRING).description("호스트.이메일"),
                                                fieldWithPath("host.nickname").type(JsonFieldType.STRING)
                                                        .description("호스트.닉네임"),
                                                fieldWithPath("host.introduction").type(JsonFieldType.VARIES)
                                                        .description("호스트.자기소개"),
                                                fieldWithPath("host.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("호스트.프로필 이미지 경로"),
                                                fieldWithPath("host.mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("호스트.매너 스코어"),
                                                fieldWithPath("host.mannerScoreCount").type(JsonFieldType.NUMBER)
                                                        .description("호스트.매너 스코어 반영 횟수"),
                                                fieldWithPath("host.addressDepth1").type(JsonFieldType.STRING)
                                                        .description("호스트.주소1(도,시)"),
                                                fieldWithPath("host.addressDepth2").type(JsonFieldType.STRING)
                                                        .description("호스트.주소2(구)"),
                                                fieldWithPath("host.positions").type(JsonFieldType.ARRAY)
                                                        .description("호스트.포지션 목록"),
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도,시)"),
                                                fieldWithPath("addressDepth2").type(JsonFieldType.STRING).description("주소2(구)"),
                                                fieldWithPath("positions").type(JsonFieldType.ARRAY).description("포지션 목록"),
                                                fieldWithPath("members[]").type(JsonFieldType.ARRAY)
                                                        .description("게스트 모집글에 참여 신청한 사용자 목록"),
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
    @DisplayName("게스트 모집 참여 신청 수락")
    void updateGameMemberRegistrationStatus_ReturnVoid() throws Exception {
        // given
        final Game game = gameSetup.saveWithWaitingMembers(2);
        final Member host = game.getHost();
        final Member guest = game.getGameMembers()
                .get(1)
                .getMember();

        final String subject = String.valueOf(host.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest = GameDtoFixtures
                .gameMemberRegistrationStatusUpdateRequestBuild(CONFIRMED);
        final String requestBody = objectMapper.writeValueAsString(gameMemberRegistrationStatusUpdateRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        patch(BASE_URL + "/{gameId}/members/{memberId}", game.getId(), guest.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                                .content(requestBody)
                )
                .andExpect(status().isNoContent());

        // then
        resultActions.andDo(document("confirm-gameMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game")
                                        .summary("게스트 모집 참여 신청 수락")
                                        .description("호스트가 게스트 모집 참여 신청을 수락 한다.")
                                        .pathParameters(
                                                parameterWithName("gameId").description("게스트 모집글 ID"),
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
    @DisplayName("게스트 모집 참여 신청 거절/취소")
    void deleteGameMember_ReturnVoid() throws Exception {
        // given
        final Game game = gameSetup.saveWithWaitingMembers(2);
        final Member host = game.getHost();
        final Member guest = game.getGameMembers()
                .get(1)
                .getMember();

        final String subject = String.valueOf(host.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        delete(BASE_URL + "/{gameId}/members/{memberId}", game.getId(), guest.getId())
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isNoContent());

        // then
        resultActions.andDo(document("delete-gameMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game")
                                        .summary("게스트 모집 참여 신청 거절/취소")
                                        .description("게스트 모집 참여 신청에 대해 거절 혹은 취소한다.")
                                        .pathParameters(
                                                parameterWithName("gameId").description("게스트 모집글 ID"),
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
    @DisplayName("다른 사용자 매너 스코어 리뷰")
    void reviewMannerScores_ReturnVoid() throws Exception {
        // given
        final Game game = gameSetup.saveWithConfirmedMembers(3);
        final Member host = game.getHost();
        final List<GameMember> gameMembers = game.getGameMembers();
        final List<Member> guests = gameMembers.subList(1, gameMembers.size())
                .stream()
                .map(GameMember::getMember)
                .toList();

        final String subject = String.valueOf(host.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        final MannerScoreReviewsRequest mannerScoreReviewsRequest = GameDtoFixtures.mannerScoreReviewsRequestBuild(
                guests);
        final String requestBody = objectMapper.writeValueAsString(mannerScoreReviewsRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        patch(BASE_URL + "/{gameId}/members/manner-scores", game.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                                .content(requestBody)
                )
                .andExpect(status().isNoContent());

        // then
        resultActions.andDo(document("review-mannerScores",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game")
                                        .summary("다른 사용자 매너 스코어 리뷰")
                                        .description("경기 종료 후, 다른 사용자의 매너 스코어를 리뷰한다.")
                                        .pathParameters(
                                                parameterWithName("gameId").description("게스트 모집글 ID")
                                        )
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .requestFields(
                                                fieldWithPath("mannerScoreReviews[]").type(JsonFieldType.ARRAY)
                                                        .description("매너 스코어 리뷰 리스트"),
                                                fieldWithPath("mannerScoreReviews[].memberId").type(JsonFieldType.NUMBER)
                                                        .description("경기에 참여한 사용자 ID"),
                                                fieldWithPath("mannerScoreReviews[].mannerScore").type(JsonFieldType.NUMBER)
                                                        .description("부여한 매너 스코어 (-1, 0, 1)")
                                        )
                                        .build()
                        )
                )
        );
    }
}
