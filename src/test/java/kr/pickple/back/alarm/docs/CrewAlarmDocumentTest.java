package kr.pickple.back.alarm.docs;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import kr.pickple.back.alarm.IntegrationCrewAlarmTest;
import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.alarm.dto.request.CrewAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.repository.CrewAlarmRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.fixture.domain.CrewAlarmFixtures;
import kr.pickple.back.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class CrewAlarmDocumentTest extends IntegrationCrewAlarmTest {

    private static final String BASE_URL = "/crew-alarms";

    @Autowired
    private CrewAlarmRepository crewAlarmRepository;

    @Test
    @DisplayName("사용자의 크루 알람에 대하여 읽음 여부 수정")
    void updateCrewAlarmStatus_ReturnVoid() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final Crew crew = crewSetup.saveWithConfirmedMembers(1);
        final CrewAlarm crewAlarm = crewAlarmRepository.save(CrewAlarmFixtures.crewAlarmBuild(member, crew));
        final CrewAlarmUpdateStatusRequest request = CrewAlarmUpdateStatusRequest.from(true);

        //when
        final ResultActions resultActions = mockMvc.perform(patch(BASE_URL + "/{crewAlarmId}", crewAlarm.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isNoContent())
                .andDo(document("update-crew-alarm-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("crew Alarm")
                                        .summary("사용자의 크루 알람에 대하여 읽음 여부 수정")
                                        .description("사용자가 보낸 크루 알람의 읽음 상태를 변경한다.")
                                        .requestFields(
                                                fieldWithPath("isRead").type(JsonFieldType.BOOLEAN)
                                                        .description("읽음 상태")
                                        )
                                        .pathParameters(
                                                parameterWithName("crewAlarmId").description("크루 알람 ID")
                                        )
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                                        )
                                        .build()
                        )
                ));
    }
}
