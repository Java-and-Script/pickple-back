package kr.pickple.back.auth.domain.oauth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthMember {

    private Long oauthId;
    private String email;
    private String profileImageUrl;
    private String nickname;
    private OauthProvider oauthProvider;
}
