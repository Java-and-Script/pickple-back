package kr.pickple.back.auth.service.authcode;

import kr.pickple.back.auth.domain.oauth.OauthProvider;

public interface AuthCodeRequestUrlProvider {

    OauthProvider oauthprovider();

    String provideUrl();
}
