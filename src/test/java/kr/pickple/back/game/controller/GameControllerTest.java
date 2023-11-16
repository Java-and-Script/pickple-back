package kr.pickple.back.game.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.fixture.setup.GameSetup;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    private static final String BASE_URL = "/games";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameSetup gameSetup;

    @Test
    @DisplayName("게스트 모집글의 상세 정보를 조회할 수 있다.")
    void findGameDetailsById_ReturnGameResponse() throws Exception {
        // given
        final Game game = gameSetup.saveWithConfirmedMembers(2);
        final Member guest = game.getGameMembers()
                .get(1)
                .getMember();

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{gameId}", game.getId()));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(game.getId()))
                .andExpect(jsonPath("content").value(game.getContent()))
                .andExpect(jsonPath("playDate").value(game.getPlayDate().toString()))
                .andExpect(jsonPath("playStartTime").value(game.getPlayStartTime().toString() + ":00"))
                .andExpect(jsonPath("playEndTime").value(game.getPlayEndTime().toString() + ":00"))
                .andExpect(jsonPath("playTimeMinutes").value(game.getPlayTimeMinutes()))
                .andExpect(jsonPath("mainAddress").value(game.getMainAddress()))
                .andExpect(jsonPath("detailAddress").value(game.getDetailAddress()))
                .andExpect(jsonPath("latitude").value(game.getPoint().getY()))
                .andExpect(jsonPath("longitude").value(game.getPoint().getX()))
                .andExpect(jsonPath("status").value(game.getStatus().getDescription()))
                .andExpect(jsonPath("viewCount").value(game.getViewCount()))
                .andExpect(jsonPath("cost").value(game.getCost()))
                .andExpect(jsonPath("memberCount").value(game.getMemberCount()))
                .andExpect(jsonPath("maxMemberCount").value(game.getMaxMemberCount()))
                .andExpect(jsonPath("host.id").value(game.getHost().getId()))
                .andExpect(jsonPath("host.email").value(game.getHost().getEmail()))
                .andExpect(jsonPath("host.nickname").value(game.getHost().getNickname()))
                .andExpect(jsonPath("host.introduction").value(game.getHost().getIntroduction()))
                .andExpect(jsonPath("host.profileImageUrl").value(game.getHost().getProfileImageUrl()))
                .andExpect(jsonPath("host.mannerScore").value(game.getHost().getMannerScore()))
                .andExpect(jsonPath("host.mannerScoreCount").value(game.getHost().getMannerScoreCount()))
                .andExpect(jsonPath("addressDepth1").value(game.getAddressDepth1().getName()))
                .andExpect(jsonPath("addressDepth2").value(game.getAddressDepth2().getName()))
                .andExpect(jsonPath("positions[0]").value(game.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("positions[1]").value(game.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("members[0].id").value(game.getHost().getId()))
                .andExpect(jsonPath("members[1].id").value(guest.getId()))
                .andDo(print());
    }
}
