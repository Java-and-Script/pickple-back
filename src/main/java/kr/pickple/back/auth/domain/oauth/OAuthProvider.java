package kr.pickple.back.auth.domain.oauth;

public enum OAuthProvider {
    KAKAO,
    ;

    public static OAuthProvider from(final String type) {
        return OAuthProvider.valueOf(type.toUpperCase());
    }
}
