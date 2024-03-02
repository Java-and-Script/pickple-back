package kr.pickple.back.chat.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.chat.IntegrationChatTest;
import kr.pickple.back.chat.dto.request.PersonalChatRoomCreateRequest;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.fixture.dto.ChatDtoFixtures;
import kr.pickple.back.member.domain.Member;

@Transactional
class ChatMessageDocumentTest extends IntegrationChatTest {

    private static final String BASE_URL = "/messages";

    @Test
    @DisplayName("특정 채팅방의 모든 메시지 목록 조회")
    void findAllMessagesInRoom_ReturnChatMessageResponses() throws Exception {
        // given
        final List<Member> members = memberSetup.save(2);
        final Member sender = members.get(0);
        final Member receiver = members.get(1);

        final PersonalChatRoomCreateRequest personalChatRoomCreateRequest = ChatDtoFixtures.personalChatRoomCreateRequestBuild(
                receiver.getId());
        final ChatRoomDetailResponse personalChatRoom = chatRoomService.createPersonalRoom(sender.getId(),
                personalChatRoomCreateRequest.getReceiverId());

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(sender.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/rooms/{roomId}", personalChatRoom.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-all-messages-in-room",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Chat")
                                        .summary("특정 채팅방의 모든 메시지 목록 조회")
                                        .description("특정 채팅방의 모든 메시지 목록을 조회한다.")
                                        .responseSchema(schema("ChatMessageResponse"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .pathParameters(
                                                parameterWithName("roomId").description("채팅방 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("[].type").type(JsonFieldType.STRING)
                                                        .description("채팅 메시지 타입"),
                                                fieldWithPath("[].content").type(JsonFieldType.STRING)
                                                        .description("채팅 메시지 내용"),
                                                fieldWithPath("[].sender").type(JsonFieldType.OBJECT)
                                                        .description("채팅 메시지 발신자"),
                                                fieldWithPath("[].sender.id").type(JsonFieldType.NUMBER)
                                                        .description("채팅 메시지 발신자의 ID"),
                                                fieldWithPath("[].sender.nickname").type(JsonFieldType.STRING)
                                                        .description("채팅 메시지 발신자의 닉네임"),
                                                fieldWithPath("[].sender.profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("채팅 메시지 발신자의 프로필 이미지 경로"),
                                                fieldWithPath("[].roomId").type(JsonFieldType.NUMBER)
                                                        .description("채팅 메시지가 수신된 채팅방 ID"),
                                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING)
                                                        .description("채팅 메시지 발신 시각")
                                        )
                                        .build()
                        )
                )
        );
    }
}
