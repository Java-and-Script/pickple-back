package kr.pickple.back.auth.service.authcode;

import kr.pickple.back.auth.domain.oauth.OAuthProvider;

public interface AuthCodeRequestUrlProvider {

    OAuthProvider oAuthProvider();

    String provideUrl();
}
