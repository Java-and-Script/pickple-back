package kr.pickple.back.member.dto.response;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.auth.domain.oauth.OauthMember;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.member.repository.entity.MemberEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthenticatedMemberResponse {

    private final String accessToken;
    private final String refreshToken;
    private final Long id;
    private final String nickname;
    private final String profileImageUrl;
    private final String email;
    private final Long oauthId;
    private final OauthProvider oauthProvider;
    private final String addressDepth1;
    private final String addressDepth2;

    @Builder
    private AuthenticatedMemberResponse(
            final String accessToken,
            final String refreshToken,
            final Long id,
            final String nickname,
            final String profileImageUrl,
            final String email,
            final Long oauthId,
            final OauthProvider oauthProvider,
            final String addressDepth1,
            final String addressDepth2
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
        this.addressDepth1 = addressDepth1;
        this.addressDepth2 = addressDepth2;
    }

    public static AuthenticatedMemberResponse of(
            final MemberEntity member,
            final AuthTokens authTokens,
            final MainAddress mainAddress
    ) {
        return AuthenticatedMemberResponse.builder()
                .accessToken(authTokens.getAccessToken())
                .refreshToken(authTokens.getRefreshToken())
                .id(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .email(member.getEmail())
                .oauthId(member.getOauthId())
                .oauthProvider(member.getOauthProvider())
                .addressDepth1(mainAddress.getAddressDepth1().getName())
                .addressDepth2(mainAddress.getAddressDepth2().getName())
                .build();
    }

    public static AuthenticatedMemberResponse of(final OauthMember oauthMember, final AuthTokens registerToken) {
        return AuthenticatedMemberResponse.builder()
                .accessToken(registerToken.getAccessToken())
                .nickname(oauthMember.getNickname())
                .profileImageUrl(oauthMember.getProfileImageUrl())
                .email(oauthMember.getEmail())
                .oauthId(oauthMember.getOauthId())
                .oauthProvider(oauthMember.getOauthProvider())
                .build();
    }
}
