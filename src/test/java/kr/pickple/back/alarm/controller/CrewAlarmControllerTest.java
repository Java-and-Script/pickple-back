package kr.pickple.back.alarm.controller;

import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.alarm.dto.request.CrewAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.repository.CrewAlarmRepository;
import kr.pickple.back.alarm.IntegrationCrewAlarmTest;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.fixture.domain.CrewAlarmFixtures;
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
public class CrewAlarmControllerTest extends IntegrationCrewAlarmTest {

    private static final String BASE_URL = "/crew-alarms";

    @Autowired
    private CrewAlarmRepository crewAlarmRepository;

    @Test
    @DisplayName("사용자는 보내진 크루 알람에 대하여 읽음 처리를 할 수 있다.")
    void updateCrewAlarmStatus_Success() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final Crew crew = crewSetup.saveWithConfirmedMembers(1);
        final CrewAlarm crewAlarm = crewAlarmRepository.save(CrewAlarmFixtures.crewAlarmBuild(member, crew));
        final CrewAlarmUpdateStatusRequest request = new CrewAlarmUpdateStatusRequest(true);

        //when
        final ResultActions resultActions = mockMvc.perform(patch(BASE_URL + "/" + crewAlarm.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isNoContent());
        final CrewAlarm updatedCrewAlarm = crewAlarmRepository.findById(crewAlarm.getId()).get();
        assertTrue(updatedCrewAlarm.getIsRead());
    }
}
