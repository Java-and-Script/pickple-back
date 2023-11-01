package kr.pickple.back.auth.service;

import org.springframework.stereotype.Service;

import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.service.authcode.AuthCodeRequestUrlProviderComposite;
import kr.pickple.back.auth.service.memberclient.OauthMemberClientComposite;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthMemberClientComposite oauthMemberClientComposite;

    public String getAuthCodeRequestUrl(final OauthProvider oauthProvider) {
        return authCodeRequestUrlProviderComposite.provide(oauthProvider);
    }

    public void processLoginOrRegistration(final OauthProvider oauthProvider, final String authCode) {
        oauthMemberClientComposite.fetch(oauthProvider, authCode);
    }
}
