package kr.pickple.back.game.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.domain.Category.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.fixture.dto.GameDtoFixtures;
import kr.pickple.back.game.IntegrationGameTest;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.request.MannerScoreReviewsRequest;
import kr.pickple.back.member.repository.entity.MemberEntity;

@Transactional
class GameEntityDocumentTest extends IntegrationGameTest {

    private static final String BASE_URL = "/games";

    //todo: 카카오 외부 API 의존성으로 인한 테스트 실패 해결 필요.

    // @Test
    // @DisplayName("게스트 모집글 생성")
    // void createGame_ReturnGameIdResponse() throws Exception {
    //     // given
    //     final GameCreateRequest gameCreateRequest = GameDtoFixtures.gameCreateRequestBuild();
    //     final String requestBody = objectMapper.writeValueAsString(gameCreateRequest);
    //
    //     final Member host = memberSetup.save();
    //     final String subject = String.valueOf(host.getId());
    //     final AuthTokens authTokens = jwtProvider.createLoginToken(subject);
    //
    //     // when
    //     final ResultActions resultActions = mockMvc.perform(post(BASE_URL)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(requestBody)
    //             .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
    //         )
    //         .andExpect(status().isCreated());
    //
    //     // then
    //     resultActions.andDo(document("create-game",
    //             preprocessRequest(prettyPrint()),
    //             preprocessResponse(prettyPrint()),
    //             resource(
    //                 ResourceSnippetParameters.builder()
    //                     .tag("Game")
    //                     .summary("게스트 모집 생성")
    //                     .description("게스트 모집을 생성한다.")
    //                     .requestSchema(schema("GameCreateRequest"))
    //                     .responseSchema(schema("GameIdResponse"))
    //                     .requestHeaders(
    //                         headerWithName(AUTHORIZATION).type(SimpleType.STRING)
    //                             .description("Access Token")
    //                     )
    //                     .requestFields(
    //                         fieldWithPath("content").type(JsonFieldType.STRING)
    //                             .description("게스트 모집글 내용"),
    //                         fieldWithPath("playDate").type(JsonFieldType.STRING).description("경기 날짜"),
    //                         fieldWithPath("playStartTime").type(JsonFieldType.STRING)
    //                             .description("경기 시작 시간"),
    //                         fieldWithPath("playTimeMinutes").type(JsonFieldType.NUMBER)
    //                             .description("경기 진행 분"),
    //                         fieldWithPath("mainAddress").type(JsonFieldType.STRING)
    //                             .description("메인 주소(도/시, 구, 동, 번지)"),
    //                         fieldWithPath("detailAddress").type(JsonFieldType.STRING)
    //                             .description("상세 주소(층, 호수)"),
    //                         fieldWithPath("latitude").type(JsonFieldType.VARIES).description("위도"),
    //                         fieldWithPath("longitude").type(JsonFieldType.VARIES).description("경도"),
    //                         fieldWithPath("cost").type(JsonFieldType.NUMBER).description("비용"),
    //                         fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER).description("인원 제한"),
    //                         fieldWithPath("positions").type(JsonFieldType.ARRAY).description("포지션 목록")
    //                     )
    //                     .responseFields(
    //                         fieldWithPath("gameId").type(JsonFieldType.NUMBER).description("게스트 모집 ID")
    //                     )
    //                     .build()
    //             )
    //         )
    //     );
    // }

    @Test
    @DisplayName("조건별(장소) 게스트 모집글 조회")
    void findGamesByCategory_ReturnGameResponses() throws Exception {
        // given
        final GameEntity gameEntity = gameSetup.saveWithConfirmedMembers(3);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        get(BASE_URL)
                                .param("category", ADDRESS.getValue())
                                .param("value", "서울시+영등포구")
                                .param("page", "0")
                                .param("size", "1")
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-games-by-category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game")
                                        .summary("조건별(장소) 게스트 모집글 조회")
                                        .description("필터링 조건을 통해 게스트 모집글 목록을 조회한다")
                                        .responseSchema(schema("GameResponse[]"))
                                        .queryParameters(
                                                parameterWithName("category").description("필터링 조건"),
                                                parameterWithName("value").description("필터링 값"),
                                                parameterWithName("page").description("페이지 시작 번호"),
                                                parameterWithName("size").description("페이지 사이즈")
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
                                                subsectionWithPath("[].positions").type(JsonFieldType.ARRAY)
                                                        .description("포지션 목록"),
                                                fieldWithPath("[].members[]").type(JsonFieldType.ARRAY)
                                                        .description("게스트 모집글에 참여 확정된 사용자 목록"),
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
                                                subsectionWithPath("[].members[].positions").type(JsonFieldType.ARRAY)
                                                        .description("주 포지션 목록")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("게스트 모집글 상세 조회")
    void findGameById_ReturnGameResponse() throws Exception {
        // given
        final GameEntity gameEntity = gameSetup.saveWithConfirmedMembers(3);

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{gameId}", gameEntity.getId()))
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
        final List<MemberEntity> members = memberSetup.save(2);
        final MemberEntity host = members.get(0);
        final MemberEntity guest = members.get(1);
        final GameEntity gameEntity = gameSetup.save(host);

        final String subject = String.valueOf(guest.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        post(BASE_URL + "/{gameId}/members", gameEntity.getId())
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
        final GameEntity gameEntity = gameSetup.saveWithWaitingMembers(3);
        final MemberEntity host = gameEntity.getHost();

        final String subject = String.valueOf(host.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        get(BASE_URL + "/{gameId}/members", gameEntity.getId())
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
        final GameEntity gameEntity = gameSetup.saveWithWaitingMembers(2);
        final MemberEntity host = gameEntity.getHost();
        final MemberEntity guest = gameEntity.getGameMembers()
                .get(1)
                .getMember();

        final String subject = String.valueOf(host.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest = GameDtoFixtures
                .gameMemberRegistrationStatusUpdateRequestBuild(CONFIRMED);
        final String requestBody = objectMapper.writeValueAsString(gameMemberRegistrationStatusUpdateRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        patch(BASE_URL + "/{gameId}/members/{memberId}", gameEntity.getId(), guest.getId())
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
                                        .requestSchema(schema("GameMemberRegistrationStatusUpdateRequest"))
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
        final GameEntity gameEntity = gameSetup.saveWithWaitingMembers(2);
        final MemberEntity host = gameEntity.getHost();
        final MemberEntity guest = gameEntity.getGameMembers()
                .get(1)
                .getMember();

        final String subject = String.valueOf(host.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        delete(BASE_URL + "/{gameId}/members/{memberId}", gameEntity.getId(), guest.getId())
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
        final GameEntity gameEntity = gameSetup.saveWithConfirmedMembers(3);
        final MemberEntity host = gameEntity.getHost();
        final List<GameMemberEntity> gameMemberEntities = gameEntity.getGameMembers();
        final List<MemberEntity> guests = gameMemberEntities.subList(1, gameMemberEntities.size())
                .stream()
                .map(GameMemberEntity::getMember)
                .toList();

        final String subject = String.valueOf(host.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        final MannerScoreReviewsRequest mannerScoreReviewsRequest = GameDtoFixtures.mannerScoreReviewsRequestBuild(
                guests);
        final String requestBody = objectMapper.writeValueAsString(mannerScoreReviewsRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        patch(BASE_URL + "/{gameId}/members/manner-scores", gameEntity.getId())
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
                                        .requestSchema(schema("MannerScoreReviewsRequest"))
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
