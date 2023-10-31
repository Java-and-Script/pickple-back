package kr.pickple.back.member.dto;

import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import kr.pickple.back.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthenticatedMemberResponse {

    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String email;
    private Long oAuthId;
    private OAuthProvider oAuthProvider;
    private String addressDepth1;
    private String addressDepth2;

    @Builder
    private AuthenticatedMemberResponse(
            final Long id,
            final String nickname,
            final String profileImageUrl,
            final String email,
            final Long oAuthId,
            final OAuthProvider oAuthProvider,
            final String addressDepth1,
            final String addressDepth2
    ) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.oAuthId = oAuthId;
        this.oAuthProvider = oAuthProvider;
        this.addressDepth1 = addressDepth1;
        this.addressDepth2 = addressDepth2;
    }

    public static AuthenticatedMemberResponse from(final Member member) {
        return AuthenticatedMemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .email(member.getEmail())
                .oAuthId(member.getOauthId())
                .oAuthProvider(member.getOauthProvider())
                .addressDepth1(member.getAddressDepth1().getName())
                .addressDepth2(member.getAddressDepth2().getName())
                .build();
    }
}
