package kr.pickple.back.position.docs;

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

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class PositionDocumentTest {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    @DisplayName("포지션 목록 조회")
    void findAllPositions_ReturnPositionResponses() throws Exception {
        // when
        final ResultActions resultActions = mockMvc.perform(get("/positions"))
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-all-positions",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Position")
                                        .summary("포지션 목록 조회")
                                        .description("전체 포지션 목록을 조회한다.")
                                        .responseSchema(schema("PositionResponse"))
                                        .responseFields(
                                                fieldWithPath("[].name").type(JsonFieldType.STRING)
                                                        .description("포지션 이름(센터, 파워 포워드, 스몰 포워드, 포인트 가드, 슈팅 가드)"),
                                                fieldWithPath("[].acronym").type(JsonFieldType.STRING)
                                                        .description("포지션 약자(C, PF, SF, PG, SG)"),
                                                fieldWithPath("[].description").type(JsonFieldType.STRING)
                                                        .description("포지션 설명")
                                        )
                                        .build()
                        )
                )
        );
    }
}
