package kr.pickple.back.auth.service.authcode;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static kr.pickple.back.auth.exception.AuthExceptionCode.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.exception.AuthException;

@Component
public class AuthCodeRequestUrlProviderComposite {

    private final Map<OauthProvider, AuthCodeRequestUrlProvider> mapping;

    public AuthCodeRequestUrlProviderComposite(final Set<AuthCodeRequestUrlProvider> providers) {
        mapping = providers.stream()
                .collect(toMap(AuthCodeRequestUrlProvider::oauthprovider, identity()));
    }

    public String provide(final OauthProvider oauthProvider) {
        return getProvider(oauthProvider).provideUrl();
    }

    private AuthCodeRequestUrlProvider getProvider(final OauthProvider oauthProvider) {
        return Optional.ofNullable(mapping.get(oauthProvider))
                .orElseThrow(() -> new AuthException(AUTH_NOT_FOUND_OAUTH_PROVIDER, oauthProvider.toString()));
    }
}
