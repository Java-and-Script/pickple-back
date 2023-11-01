package kr.pickple.back.auth.dto.kakao;

import kr.pickple.back.auth.domain.oauth.OauthMember;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoMemberResponse {

    private Long id;
    private KakaoAccount kakaoAccount;

    public OauthMember toOauthMember() {
        return OauthMember.builder()
                .id(id)
                .email(kakaoAccount.email)
                .nickname(kakaoAccount.profile.nickname)
                .profileImageUrl(kakaoAccount.profile.profileImageUrl)
                .oauthProvider(OauthProvider.KAKAO)
                .build();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class KakaoAccount {

        private String email;
        private Profile profile;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class Profile {

        private String nickname;
        private String profileImageUrl;
    }
}
