package kr.pickple.back.auth.service;

import org.springframework.stereotype.Service;

import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import kr.pickple.back.auth.service.authcode.AuthCodeRequestUrlProviderComposite;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;

    public String getAuthCodeRequestUrl(final OAuthProvider oAuthProvider) {
        return authCodeRequestUrlProviderComposite.provide(oAuthProvider);
    }
}
