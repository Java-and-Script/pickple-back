package kr.pickple.back.auth.domain.oauth;

public enum OauthProvider {
    KAKAO,
    ;

    public static OauthProvider from(final String type) {
        return OauthProvider.valueOf(type.toUpperCase());
    }
}
