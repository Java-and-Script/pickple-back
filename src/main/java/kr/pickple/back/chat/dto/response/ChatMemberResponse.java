package kr.pickple.back.chat.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMemberResponse {

    private Long id;
    private String nickname;
    private String profileImageUrl;
}
