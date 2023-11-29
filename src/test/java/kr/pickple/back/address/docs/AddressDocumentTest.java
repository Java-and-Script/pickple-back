package kr.pickple.back.address.docs;

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
class AddressDocumentTest {

    @Autowired
    protected MockMvc mockMvc;

    @Test
    @DisplayName("지역 목록 조회")
    void findAllAddress_ReturnAllAddressResponse() throws Exception {
        // when
        final ResultActions resultActions = mockMvc.perform(get("/address"))
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-all-address",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Address")
                                        .summary("지역 목록 조회")
                                        .description("전체 지역 목록을 조회한다.")
                                        .responseSchema(schema("AllAddressResponse"))
                                        .responseFields(
                                                fieldWithPath("addressDepth1").type(JsonFieldType.STRING)
                                                        .description("주소1(도, 시)"),
                                                fieldWithPath("addressDepth2List").type(JsonFieldType.ARRAY)
                                                        .description("주소1에 속한 주소2(구) 목록")
                                        )
                                        .build()
                        )
                )
        );
    }
}
