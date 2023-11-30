package kr.pickple.back.alarm.controller;

import kr.pickple.back.alarm.IntegrationAlarmTest;
import kr.pickple.back.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class AlarmControllerTest extends IntegrationAlarmTest {

    private static final String BASE_URL = "/alarms";

    @Test
    @DisplayName("사용자는 재접속 시 읽지 않은 알람이 있는지 알 수 있다.")
    void findUnreadAlarm_Success() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        //when & then
        mockMvc.perform(get(BASE_URL + "/unread")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.unread").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자는 해당 사용자에게 온 모든 알람 목록을 조회할 수 있다.")
    void findAllAlarms_Success() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        //when & then
        mockMvc.perform(get(BASE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("size", "6"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.alarmResponse").exists())
                .andExpect(jsonPath("$.hasNext").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자는 해당 사용자에게 온 모든 알람을 모두 삭제할 수 있다.")
    void deleteAllAlarms_Success() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        //when & then
        mockMvc.perform(delete(BASE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
