package kr.pickple.back.member.domain;

import java.util.List;

import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NewMember {

    private Long memberId;
    private AuthTokens authTokens;
    private String nickname;
    private String profileImageUrl;
    private String email;
    private List<Position> positions;
    private Long oauthId;
    private OauthProvider oauthProvider;
    private String addressDepth1Name;
    private String addressDepth2Name;

    public void updateMemberId(final Long memberId) {
        this.memberId = memberId;
    }

    public void updateAuthTokens(final AuthTokens authTokens) {
        this.authTokens = authTokens;
    }
}
