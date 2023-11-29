package kr.pickple.back.chat.docs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.Schema.*;
import static kr.pickple.back.chat.domain.RoomType.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
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
class ChatRoomDocumentTest extends IntegrationChatTest {

    private static final String BASE_URL = "/rooms";

    @Test
    @DisplayName("새 1:1 채팅방 생성")
    void createPersonalRoom_ReturnChatRoomDetailResponse() throws Exception {
        // given
        final List<Member> members = memberSetup.save(2);
        final Member sender = members.get(0);
        final Member receiver = members.get(1);

        final PersonalChatRoomCreateRequest personalChatRoomCreateRequest = ChatDtoFixtures.personalChatRoomCreateRequestBuild(
                receiver.getId());
        chatRoomService.createPersonalRoom(sender.getId(), personalChatRoomCreateRequest);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(sender.getId()));
        final String requestBody = objectMapper.writeValueAsString(personalChatRoomCreateRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                        post(BASE_URL + "/personal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        // then
        resultActions.andDo(document("create-personal-room",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Chat")
                                        .summary("새 1:1 채팅방 생성")
                                        .description("다른 사용자와의 새 1:1 채팅방을 생성한다.")
                                        .requestSchema(schema("PersonalChatRoomCreateRequest"))
                                        .responseSchema(schema("ChatRoomDetailResponse"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .requestFields(
                                                fieldWithPath("receiverId").type(JsonFieldType.NUMBER)
                                                        .description("수신자(상대방) ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                                        .description("새로 생성된 채팅방 ID"),
                                                fieldWithPath("roomName").type(JsonFieldType.STRING)
                                                        .description("채팅방 이름"),
                                                fieldWithPath("roomIconImageUrl").type(JsonFieldType.STRING)
                                                        .description("채팅방 아이콘 이미지 경로"),
                                                fieldWithPath("type").type(JsonFieldType.STRING)
                                                        .description("채팅방 타입"),
                                                fieldWithPath("domainId").type(JsonFieldType.NUMBER)
                                                        .description("채팅방의 도메인 ID"),
                                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER)
                                                        .description("현재 채팅방 인원수"),
                                                fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("채팅방 인원제한"),
                                                fieldWithPath("playStartTime").type(JsonFieldType.NULL)
                                                        .description("경기 시작 시각"),
                                                fieldWithPath("playTimeMinutes").type(JsonFieldType.NULL)
                                                        .description("경기 진행 시간"),
                                                fieldWithPath("members[]").type(JsonFieldType.ARRAY)
                                                        .description("채팅방에 참여한 사용자 목록"),
                                                fieldWithPath("members[].id").type(JsonFieldType.NUMBER)
                                                        .description("채팅방에 참여한 사용자의 ID"),
                                                fieldWithPath("members[].nickname").type(JsonFieldType.STRING)
                                                        .description("채팅방에 참여한 사용자의 닉네임"),
                                                fieldWithPath("members[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("채팅방에 참여한 사용자의 프로필 이미지 경로"),
                                                fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                        .description("채팅방 생성 시각")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("특정 사용자와의 1:1 채팅방 존재 여부 조회")
    void findActivePersonalChatRoomWithReceiver_ReturnPersonalChatRoomExistedResponse() throws Exception {
        // given
        final List<Member> members = memberSetup.save(2);
        final Member sender = members.get(0);
        final Member receiver = members.get(1);

        final PersonalChatRoomCreateRequest personalChatRoomCreateRequest = ChatDtoFixtures.personalChatRoomCreateRequestBuild(
                receiver.getId());
        chatRoomService.createPersonalRoom(sender.getId(), personalChatRoomCreateRequest);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(sender.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(
                        get(BASE_URL + "/personal")
                                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                                .param("receiver", String.valueOf(receiver.getId()))
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-active-personal-chatroom-with-receiver",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Chat")
                                        .summary("특정 사용자와의 1:1 채팅방 존재 여부 조회")
                                        .description("특정 사용자와의 1:1 채팅방 존재 여부를 조회한다.")
                                        .responseSchema(schema("PersonalChatRoomExistedResponse"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .queryParameters(
                                                parameterWithName("receiver").description("수신자(상대방) ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("roomId").type(JsonFieldType.NUMBER)
                                                        .description("1:1 채팅방 ID"),
                                                fieldWithPath("isSenderActive").type(JsonFieldType.BOOLEAN)
                                                        .description("현재 사용자가 상대방과의 채팅방에 입장 상태라면 true, 퇴장 상태라면 false")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("채팅방 타입에 따른 참여중인 모든 채팅방 목록 조회")
    void findAllActiveChatRoomsByType_ReturnChatRoomResponses() throws Exception {
        // given
        final List<Member> members = memberSetup.save(3);
        final Member sender = members.get(0);
        final Member receiver1 = members.get(1);
        final Member receiver2 = members.get(2);

        final PersonalChatRoomCreateRequest personalChatRoomCreateRequest1 = ChatDtoFixtures.personalChatRoomCreateRequestBuild(
                receiver1.getId());
        chatRoomService.createPersonalRoom(sender.getId(), personalChatRoomCreateRequest1);

        final PersonalChatRoomCreateRequest personalChatRoomCreateRequest2 = ChatDtoFixtures.personalChatRoomCreateRequestBuild(
                receiver2.getId());
        chatRoomService.createPersonalRoom(sender.getId(), personalChatRoomCreateRequest2);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(sender.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                        .param("type", PERSONAL.getDescription())
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-all-active-chatrooms-by-type",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Chat")
                                        .summary("채팅방 타입에 따른 참여중인 모든 채팅방 목록 조회")
                                        .description("채팅방 타입에 따른 참여중인 모든 채팅방 목록을 조회한다.")
                                        .responseSchema(schema("ChatRoomResponse"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .queryParameters(
                                                parameterWithName("type").description("채팅방 타입 (개인, 게스트, 크루)")
                                        )
                                        .responseFields(
                                                fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                                                        .description("채팅방 ID"),
                                                fieldWithPath("[].roomName").type(JsonFieldType.STRING)
                                                        .description("채팅방 이름"),
                                                fieldWithPath("[].roomIconImageUrl").type(JsonFieldType.STRING)
                                                        .description("채팅방 아이콘 이미지 경로"),
                                                fieldWithPath("[].type").type(JsonFieldType.STRING)
                                                        .description("채팅방 타입 (개인, 게스트, 크루)"),
                                                fieldWithPath("[].memberCount").type(JsonFieldType.NUMBER)
                                                        .description("현재 채팅방 인원수"),
                                                fieldWithPath("[].maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("채팅방 인원제한"),
                                                fieldWithPath("[].playStartTime").type(JsonFieldType.NULL)
                                                        .description("경기 시작 시각"),
                                                fieldWithPath("[].playTimeMinutes").type(JsonFieldType.NULL)
                                                        .description("경기 진행 시간"),
                                                fieldWithPath("[].lastMessageContent").type(JsonFieldType.STRING)
                                                        .description("마지막 메시지"),
                                                fieldWithPath("[].lastMessageCreatedAt").type(JsonFieldType.STRING)
                                                        .description("마지막 메시지 전송 시각"),
                                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING)
                                                        .description("채팅방 생성 시각")
                                        )
                                        .build()
                        )
                )
        );
    }

    @Test
    @DisplayName("단일 채팅방 정보 상세 조회")
    void findChatRoomById_ReturnChatRoomDetailResponse() throws Exception {
        // given
        final List<Member> members = memberSetup.save(2);
        final Member sender = members.get(0);
        final Member receiver = members.get(1);

        final PersonalChatRoomCreateRequest personalChatRoomCreateRequest = ChatDtoFixtures.personalChatRoomCreateRequestBuild(
                receiver.getId());
        final ChatRoomDetailResponse personalChatRoom = chatRoomService.createPersonalRoom(sender.getId(),
                personalChatRoomCreateRequest);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(sender.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{roomId}", personalChatRoom.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                )
                .andExpect(status().isOk());

        // then
        resultActions.andDo(document("find-chatroom-by-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Chat")
                                        .summary("단일 채팅방 정보 상세 조회")
                                        .description("단일 채팅방 정보를 상세 조회한다.")
                                        .responseSchema(schema("ChatRoomDetailResponse"))
                                        .requestHeaders(
                                                headerWithName(AUTHORIZATION).type(SimpleType.STRING)
                                                        .description("Access Token")
                                        )
                                        .pathParameters(
                                                parameterWithName("roomId").description("채팅방 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                                        .description("새로 생성된 채팅방 ID"),
                                                fieldWithPath("roomName").type(JsonFieldType.STRING)
                                                        .description("채팅방 이름"),
                                                fieldWithPath("roomIconImageUrl").type(JsonFieldType.STRING)
                                                        .description("채팅방 아이콘 이미지 경로"),
                                                fieldWithPath("type").type(JsonFieldType.STRING)
                                                        .description("채팅방 타입"),
                                                fieldWithPath("domainId").type(JsonFieldType.NUMBER)
                                                        .description("채팅방의 도메인 ID"),
                                                fieldWithPath("memberCount").type(JsonFieldType.NUMBER)
                                                        .description("현재 채팅방 인원수"),
                                                fieldWithPath("maxMemberCount").type(JsonFieldType.NUMBER)
                                                        .description("채팅방 인원제한"),
                                                fieldWithPath("playStartTime").type(JsonFieldType.NULL)
                                                        .description("경기 시작 시각"),
                                                fieldWithPath("playTimeMinutes").type(JsonFieldType.NULL)
                                                        .description("경기 진행 시간"),
                                                fieldWithPath("members[]").type(JsonFieldType.ARRAY)
                                                        .description("채팅방에 참여한 사용자 목록"),
                                                fieldWithPath("members[].id").type(JsonFieldType.NUMBER)
                                                        .description("채팅방에 참여한 사용자의 ID"),
                                                fieldWithPath("members[].nickname").type(JsonFieldType.STRING)
                                                        .description("채팅방에 참여한 사용자의 닉네임"),
                                                fieldWithPath("members[].profileImageUrl").type(JsonFieldType.STRING)
                                                        .description("채팅방에 참여한 사용자의 프로필 이미지 경로"),
                                                fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                        .description("채팅방 생성 시각")
                                        )
                                        .build()
                        )
                )
        );
    }
}
