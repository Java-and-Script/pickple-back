package kr.pickple.back.game.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

import kr.pickple.back.fixture.setup.GameSetup;
import kr.pickple.back.game.domain.Game;

@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class GameDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameSetup gameSetup;

    @Test
    @DisplayName("게스트 모집글 상세 조회")
    void findGameById_ReturnGameResponse() throws Exception {
        // given
        final Game game = gameSetup.save();

        // when
        final ResultActions resultActions = mockMvc.perform(get("/games/{gameId}", game.getId()))
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-game",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Game")
                                        .summary("게스트 모집 상세 조회")
                                        .description("게스트 모집 정보를 상세 조회한다")
                                        .responseSchema(schema("GameResponse"))
                                        .pathParameters(
                                                parameterWithName("gameId").description("게스트 모집 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게스트 모집 ID"),
                                                fieldWithPath("content").type(JsonFieldType.STRING)
                                                        .description("게스트 모집글(경기) 내용"),
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
                                                fieldWithPath("members").type(JsonFieldType.ARRAY)
                                                        .description("경기에 참여 확정된 사용자 목록")
                                        )
                                        .build()
                        )
                )
        );
    }
}
