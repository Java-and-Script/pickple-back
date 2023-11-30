package kr.pickple.back.alarm.docs;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import kr.pickple.back.alarm.IntegrationAlarmTest;
import kr.pickple.back.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class AlarmDocumentTest extends IntegrationAlarmTest {

    private static final String BASE_URL = "/alarms";

    @Test
    @DisplayName("사용자의 재접속 시 읽지 않은 알람이 있는지 확인")
    void findUnreadAlarm_ReturnAlarmExistStatusResponse() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        //when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/unread")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.unread").exists())
                .andDo(document("find-unread-alarm",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Alarm")
                                        .summary("사용자의 재접속 시 읽지 않은 알람이 있는지 확인")
                                        .description("사용자의 재접속 시 읽지 않은 알람이 있는지 확인한다.")
                                        .responseFields(
                                                fieldWithPath("unread").type(JsonFieldType.BOOLEAN)
                                                        .description("읽지 않은 알람 존재 여부")
                                        )
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("해당 사용자에게 온 모든 알람 목록을 조회")
    void findAllAlarms_ReturnAlarmResponse() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        //when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .param("size", "6"));

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.alarmResponse").exists())
                .andExpect(jsonPath("$.hasNext").exists())
                .andDo(document("find-all-alarms",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Alarm")
                                        .summary("해당 사용자에게 온 모든 알람 목록을 조회")
                                        .description("해당 사용자에게 온 모든 알람 목록을 조회한다.")
                                        .responseFields(
                                                fieldWithPath("alarmResponse").type(JsonFieldType.ARRAY)
                                                        .description("알람 응답 목록"),
                                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN)
                                                        .description("다음 페이지 존재 여부"),
                                                fieldWithPath("cursorId").type(JsonFieldType.NUMBER).optional()
                                                        .description("커서 ID. null일 경우 다음 페이지 없음")
                                        )
                                        .queryParameters(
                                                parameterWithName("size").description("페이지 크기").optional()
                                        )
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("해당 사용자에게 온 모든 알람을 모두 삭제")
    void deleteAllAlarms_ReturnVoid() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        //when
        final ResultActions resultActions = mockMvc.perform(delete(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));

        //then
        resultActions
                .andExpect(status().isNoContent())
                .andDo(document("delete-all-alarms",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Alarm")
                                        .summary("해당 사용자에게 온 모든 알람을 모두 삭제")
                                        .description("해당 사용자에게 온 모든 알람을 모두 삭제한다.")
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                                        )
                                        .build()
                        )
                ));
    }
}
