package kr.pickple.back.chat.dto.response;

import kr.pickple.back.member.domain.Member;
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

    public static ChatMemberResponse from(final Member member) {
        return ChatMemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
