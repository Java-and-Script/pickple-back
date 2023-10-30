package kr.pickple.back.auth.dto.kakao;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import kr.pickple.back.auth.domain.oauth.OAuthMember;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@JsonNaming(SnakeCaseStrategy.class)
public class KakaoMemberResponse {

    private Long id;
    private LocalDateTime connectedAt;
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor(access = PRIVATE)
    @JsonNaming(SnakeCaseStrategy.class)
    static class KakaoAccount {

        private String email;
        private Profile profile;
    }

    @Getter
    @NoArgsConstructor(access = PRIVATE)
    @JsonNaming(SnakeCaseStrategy.class)
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
