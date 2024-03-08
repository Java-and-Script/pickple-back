package kr.pickple.back.game.controller;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.fixture.dto.GameDtoFixtures;
import kr.pickple.back.game.IntegrationGameTest;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.request.MannerScoreReviewsRequest;
import kr.pickple.back.member.repository.entity.MemberEntity;

@Transactional
class GameEntityControllerTest extends IntegrationGameTest {

    private static final String BASE_URL = "/games";

    @Test
    @DisplayName("사용자는 게스트 모집글의 상세 정보를 조회할 수 있다.")
    void findGameDetailsById_ReturnGameResponse() throws Exception {
        // given
        final GameEntity gameEntity = gameSetup.saveWithConfirmedMembers(2);
        final MemberEntity host = gameEntity.getHost();
        final MemberEntity guest = gameEntity.getGameMembers()
                .get(1)
                .getMember();

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{gameId}", gameEntity.getId()));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(gameEntity.getId()))
                .andExpect(jsonPath("content").value(gameEntity.getContent()))
                .andExpect(jsonPath("playDate").value(gameEntity.getPlayDate().toString()))
                .andExpect(jsonPath("playStartTime").value(gameEntity.getPlayStartTime().toString() + ":00"))
                .andExpect(jsonPath("playEndTime").value(gameEntity.getPlayEndTime().toString() + ":00"))
                .andExpect(jsonPath("playTimeMinutes").value(gameEntity.getPlayTimeMinutes()))
                .andExpect(jsonPath("mainAddress").value(gameEntity.getMainAddress()))
                .andExpect(jsonPath("detailAddress").value(gameEntity.getDetailAddress()))
                .andExpect(jsonPath("latitude").value(gameEntity.getPoint().getY()))
                .andExpect(jsonPath("longitude").value(gameEntity.getPoint().getX()))
                .andExpect(jsonPath("status").value(gameEntity.getStatus().getDescription()))
                .andExpect(jsonPath("viewCount").value(gameEntity.getViewCount()))
                .andExpect(jsonPath("cost").value(gameEntity.getCost()))
                .andExpect(jsonPath("memberCount").value(gameEntity.getMemberCount()))
                .andExpect(jsonPath("maxMemberCount").value(gameEntity.getMaxMemberCount()))
                .andExpect(jsonPath("host.id").value(host.getId()))
                .andExpect(jsonPath("host.email").value(host.getEmail()))
                .andExpect(jsonPath("host.nickname").value(host.getNickname()))
                .andExpect(jsonPath("host.introduction").value(host.getIntroduction()))
                .andExpect(jsonPath("host.profileImageUrl").value(host.getProfileImageUrl()))
                .andExpect(jsonPath("host.mannerScore").value(host.getMannerScore()))
                .andExpect(jsonPath("host.mannerScoreCount").value(host.getMannerScoreCount()))
                .andExpect(jsonPath("addressDepth1").value(gameEntity.getAddressDepth1().getName()))
                .andExpect(jsonPath("addressDepth2").value(gameEntity.getAddressDepth2().getName()))
                .andExpect(jsonPath("positions[0]").value(gameEntity.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("positions[1]").value(gameEntity.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("members[0].id").value(host.getId()))
                .andExpect(jsonPath("members[1].id").value(guest.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("사용자는 게스트 모집글에 참여 신청을 할 수 있다.")
    void registerGameMember_Success() throws Exception {
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
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("호스트는 게스트 모집글에 참여 신청된 사용자 정보 목록을 조회할 수 있다.")
    void findAllGameMembers_WaitingStatus_ReturnGameResponse() throws Exception {
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
                get(BASE_URL + "/{gameId}/members", gameEntity.getId())
                        .param("status", WAITING.getDescription())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(gameEntity.getId()))
                .andExpect(jsonPath("content").value(gameEntity.getContent()))
                .andExpect(jsonPath("playDate").value(gameEntity.getPlayDate().toString()))
                .andExpect(jsonPath("playStartTime").value(gameEntity.getPlayStartTime().toString() + ":00"))
                .andExpect(jsonPath("playEndTime").value(gameEntity.getPlayEndTime().toString() + ":00"))
                .andExpect(jsonPath("playTimeMinutes").value(gameEntity.getPlayTimeMinutes()))
                .andExpect(jsonPath("mainAddress").value(gameEntity.getMainAddress()))
                .andExpect(jsonPath("detailAddress").value(gameEntity.getDetailAddress()))
                .andExpect(jsonPath("latitude").value(gameEntity.getPoint().getY()))
                .andExpect(jsonPath("longitude").value(gameEntity.getPoint().getX()))
                .andExpect(jsonPath("status").value(gameEntity.getStatus().getDescription()))
                .andExpect(jsonPath("viewCount").value(gameEntity.getViewCount()))
                .andExpect(jsonPath("cost").value(gameEntity.getCost()))
                .andExpect(jsonPath("memberCount").value(gameEntity.getMemberCount()))
                .andExpect(jsonPath("maxMemberCount").value(gameEntity.getMaxMemberCount()))
                .andExpect(jsonPath("host.id").value(host.getId()))
                .andExpect(jsonPath("host.email").value(host.getEmail()))
                .andExpect(jsonPath("host.nickname").value(host.getNickname()))
                .andExpect(jsonPath("host.introduction").value(host.getIntroduction()))
                .andExpect(jsonPath("host.profileImageUrl").value(host.getProfileImageUrl()))
                .andExpect(jsonPath("host.mannerScore").value(host.getMannerScore()))
                .andExpect(jsonPath("host.mannerScoreCount").value(host.getMannerScoreCount()))
                .andExpect(jsonPath("addressDepth1").value(gameEntity.getAddressDepth1().getName()))
                .andExpect(jsonPath("addressDepth2").value(gameEntity.getAddressDepth2().getName()))
                .andExpect(jsonPath("positions[0]").value(gameEntity.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("positions[1]").value(gameEntity.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("members[0].id").value(guest.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("사용자는 게스트 모집글에 참여 확정된 사용자 정보 목록을 조회할 수 있다.")
    void findAllGameMembers_ConfirmedStatus_ReturnGameResponse() throws Exception {
        // given
        final GameEntity gameEntity = gameSetup.saveWithConfirmedMembers(2);
        final MemberEntity host = gameEntity.getHost();
        final MemberEntity guest = gameEntity.getGameMembers()
                .get(1)
                .getMember();

        final String subject = String.valueOf(guest.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(
                get(BASE_URL + "/{gameId}/members", gameEntity.getId())
                        .param("status", CONFIRMED.getDescription())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(gameEntity.getId()))
                .andExpect(jsonPath("content").value(gameEntity.getContent()))
                .andExpect(jsonPath("playDate").value(gameEntity.getPlayDate().toString()))
                .andExpect(jsonPath("playStartTime").value(gameEntity.getPlayStartTime().toString() + ":00"))
                .andExpect(jsonPath("playEndTime").value(gameEntity.getPlayEndTime().toString() + ":00"))
                .andExpect(jsonPath("playTimeMinutes").value(gameEntity.getPlayTimeMinutes()))
                .andExpect(jsonPath("mainAddress").value(gameEntity.getMainAddress()))
                .andExpect(jsonPath("detailAddress").value(gameEntity.getDetailAddress()))
                .andExpect(jsonPath("latitude").value(gameEntity.getPoint().getY()))
                .andExpect(jsonPath("longitude").value(gameEntity.getPoint().getX()))
                .andExpect(jsonPath("status").value(gameEntity.getStatus().getDescription()))
                .andExpect(jsonPath("viewCount").value(gameEntity.getViewCount()))
                .andExpect(jsonPath("cost").value(gameEntity.getCost()))
                .andExpect(jsonPath("memberCount").value(gameEntity.getMemberCount()))
                .andExpect(jsonPath("maxMemberCount").value(gameEntity.getMaxMemberCount()))
                .andExpect(jsonPath("host.id").value(host.getId()))
                .andExpect(jsonPath("host.email").value(host.getEmail()))
                .andExpect(jsonPath("host.nickname").value(host.getNickname()))
                .andExpect(jsonPath("host.introduction").value(host.getIntroduction()))
                .andExpect(jsonPath("host.profileImageUrl").value(host.getProfileImageUrl()))
                .andExpect(jsonPath("host.mannerScore").value(host.getMannerScore()))
                .andExpect(jsonPath("host.mannerScoreCount").value(host.getMannerScoreCount()))
                .andExpect(jsonPath("addressDepth1").value(gameEntity.getAddressDepth1().getName()))
                .andExpect(jsonPath("addressDepth2").value(gameEntity.getAddressDepth2().getName()))
                .andExpect(jsonPath("positions[0]").value(gameEntity.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("positions[1]").value(gameEntity.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("members[0].id").value(host.getId()))
                .andExpect(jsonPath("members[1].id").value(guest.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("호스트는 다른 사용자의 게스트 모집글 참여 신청을 수락할 수 있다.")
    void updateGameMemberRegistrationStatus_Success() throws Exception {
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
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("호스트는 다른 사용자의 게스트 모집글 참여 신청을 거절할 수 있다.")
    void deleteGameMember_Host_Success() throws Exception {
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
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자는 자신의 게스트 모집글 참여 신청을 취소할 수 있다.")
    void deleteGameMember_GuestSelf_Success() throws Exception {
        // given
        final GameEntity gameEntity = gameSetup.saveWithWaitingMembers(2);
        final MemberEntity guest = gameEntity.getGameMembers()
                .get(1)
                .getMember();

        final String subject = String.valueOf(guest.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(
                delete(BASE_URL + "/{gameId}/members/{memberId}", gameEntity.getId(), guest.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자는 경기에 참여한 다른 사용자의 매너 스코어를 리뷰할 수 있다.")
    void reviewMannerScores_Success() throws Exception {
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
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }
}
