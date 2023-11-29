package kr.pickple.back.fixture.dto;

import kr.pickple.back.chat.dto.request.PersonalChatRoomCreateRequest;

public class ChatDtoFixtures {

    public static PersonalChatRoomCreateRequest personalChatRoomCreateRequestBuild(final Long receiverId) {
        return PersonalChatRoomCreateRequest.builder()
                .receiverId(receiverId)
                .build();
    }
}
