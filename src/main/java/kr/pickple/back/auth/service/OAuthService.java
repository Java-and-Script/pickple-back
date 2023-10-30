package kr.pickple.back.auth.service;

import org.springframework.stereotype.Service;

import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import kr.pickple.back.auth.service.authcode.AuthCodeRequestUrlProviderComposite;
import kr.pickple.back.auth.service.memberclient.OAuthMemberClientComposite;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OAuthMemberClientComposite oAuthMemberClientComposite;

    public String getAuthCodeRequestUrl(final OAuthProvider oAuthProvider) {
        return authCodeRequestUrlProviderComposite.provide(oAuthProvider);
    }

    public void processLoginOrRegistration(final OAuthProvider oAuthProvider, final String authCode) {
        oAuthMemberClientComposite.fetch(oAuthProvider, authCode);
    }
}
