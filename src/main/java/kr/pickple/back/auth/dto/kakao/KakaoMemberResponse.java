package kr.pickple.back.auth.dto.kakao;

import static lombok.AccessLevel.*;

import kr.pickple.back.auth.domain.oauth.OAuthMember;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class KakaoMemberResponse {

    private Long id;
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor(access = PRIVATE)
    static class KakaoAccount {

        private String email;
        private Profile profile;
    }

    @Getter
    @NoArgsConstructor(access = PRIVATE)
    static class Profile {

        private String nickname;
        private String profileImageUrl;
    }

    public OAuthMember toOAuthMember() {
        return OAuthMember.builder()
                .id(id)
                .email(kakaoAccount.email)
                .nickname(kakaoAccount.profile.nickname)
                .profileImageUrl(kakaoAccount.profile.profileImageUrl)
                .oAuthProvider(OAuthProvider.KAKAO)
                .build();
    }
}
