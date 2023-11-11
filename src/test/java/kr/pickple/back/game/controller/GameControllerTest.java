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

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {

    private static final String BASE_URL = "/games";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameSetup gameSetup;

    @Test
    @DisplayName("게임 상세 정보를 조회할 수 있다.")
    void findGameById_ReturnGameResponse() throws Exception {
        // given
        final Game savedGame = gameSetup.save();

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{games}", savedGame.getId()));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(savedGame.getId()))
                .andExpect(jsonPath("content").value(savedGame.getContent()))
                .andExpect(jsonPath("playDate").value(savedGame.getPlayDate().toString()))
                .andExpect(jsonPath("playStartTime").value(savedGame.getPlayStartTime().toString() + ":00"))
                .andExpect(jsonPath("playEndTime").value(savedGame.getPlayEndTime().toString() + ":00"))
                .andExpect(jsonPath("playTimeMinutes").value(savedGame.getPlayTimeMinutes()))
                .andExpect(jsonPath("mainAddress").value(savedGame.getMainAddress()))
                .andExpect(jsonPath("detailAddress").value(savedGame.getDetailAddress()))
                .andExpect(jsonPath("latitude").value(savedGame.getLatitude()))
                .andExpect(jsonPath("longitude").value(savedGame.getLongitude()))
                .andExpect(jsonPath("status").value(savedGame.getStatus().getDescription()))
                .andExpect(jsonPath("viewCount").value(savedGame.getViewCount()))
                .andExpect(jsonPath("cost").value(savedGame.getCost()))
                .andExpect(jsonPath("memberCount").value(savedGame.getMemberCount()))
                .andExpect(jsonPath("maxMemberCount").value(savedGame.getMaxMemberCount()))
                .andExpect(jsonPath("host.id").value(savedGame.getHost().getId()))
                .andExpect(jsonPath("host.email").value(savedGame.getHost().getEmail()))
                .andExpect(jsonPath("host.nickname").value(savedGame.getHost().getNickname()))
                .andExpect(jsonPath("host.introduction").value(savedGame.getHost().getIntroduction()))
                .andExpect(jsonPath("host.profileImageUrl").value(savedGame.getHost().getProfileImageUrl()))
                .andExpect(jsonPath("host.mannerScore").value(savedGame.getHost().getMannerScore()))
                .andExpect(jsonPath("host.mannerScoreCount").value(savedGame.getHost().getMannerScoreCount()))
                .andExpect(jsonPath("addressDepth1").value(savedGame.getAddressDepth1().getName()))
                .andExpect(jsonPath("addressDepth2").value(savedGame.getAddressDepth2().getName()))
                .andExpect(jsonPath("positions[0]").value(savedGame.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("positions[1]").value(savedGame.getPositions().get(1).getAcronym()))
                .andDo(print());
    }
}
