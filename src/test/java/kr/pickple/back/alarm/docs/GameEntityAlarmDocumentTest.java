package kr.pickple.back.alarm.docs;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import kr.pickple.back.alarm.IntegrationAlarmTest;
import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.dto.request.GameAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.repository.GameAlarmRepository;
import kr.pickple.back.fixture.domain.GameAlarmFixtures;
import kr.pickple.back.fixture.setup.GameSetup;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.member.domain.Member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameEntityAlarmDocumentTest extends IntegrationAlarmTest {

    private static final String BASE_URL = "/game-alarms";

    @Autowired
    protected GameSetup gameSetup;

    @Autowired
    private GameAlarmRepository gameAlarmRepository;

    @Test
    @DisplayName("사용자의 게임 알람에 대하여 읽음 여부 수정")
    void updateGameAlarmStatus_ReturnVoid() throws Exception {
        //given
        final Member member = memberSetup.save();
        final String accessToken = jwtProvider.createLoginToken(member.getId().toString()).getAccessToken();
        final GameEntity gameEntity = gameSetup.saveWithConfirmedMembers(1);
        final GameAlarm gameAlarm = gameAlarmRepository.save(GameAlarmFixtures.gameAlarmBuild(member, gameEntity));
        final GameAlarmUpdateStatusRequest request = GameAlarmUpdateStatusRequest.from(true);

        //when
        final ResultActions resultActions = mockMvc.perform(patch(BASE_URL + "/{gameAlarmId}", gameAlarm.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
                .andExpect(status().isNoContent())
                .andDo(document("update-game-alarm-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game Alarm")
                                        .summary("사용자의 게임 알람에 대하여 읽음 여부 수정")
                                        .description("사용자가 보낸 게임 알람의 읽음 상태를 변경한다.")
                                        .requestFields(
                                                fieldWithPath("isRead").type(JsonFieldType.BOOLEAN)
                                                        .description("읽음 상태")
                                        )
                                        .pathParameters(
                                                parameterWithName("gameAlarmId").description("게임 알람 ID")
                                        )
                                        .requestHeaders(
                                                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰")
                                        )
                                        .build()
                        )
                ));
    }
}
