package kr.pickple.back.alarm.controller;

import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.dto.request.GameAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.repository.GameAlarmRepository;
import kr.pickple.back.alarm.IntegrationGameAlarmTest;
import kr.pickple.back.fixture.domain.GameAlarmFixtures;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class GameAlarmControllerTest extends IntegrationGameAlarmTest {

    private static final String BASE_URL = "/game-alarms";

    @Autowired
    private GameAlarmRepository gameAlarmRepository;

    @Test
    @DisplayName("사용자는 보내진 게임 알람에 대하여 읽음 처리를 할 수 있다.")
    void updateGameAlarmStatus_Success() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final Game game = gameSetup.saveWithConfirmedMembers(1);
        final GameAlarm gameAlarm = gameAlarmRepository.save(GameAlarmFixtures.gameAlarmBuild(member, game));
        final GameAlarmUpdateStatusRequest request = GameAlarmUpdateStatusRequest.from(true);

        //when
        final ResultActions resultActions = mockMvc.perform(patch(BASE_URL + "/" + gameAlarm.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isNoContent());
        final GameAlarm updatedGameAlarm = gameAlarmRepository.findById(gameAlarm.getId()).get();
        assertTrue(updatedGameAlarm.getIsRead());
    }
}
