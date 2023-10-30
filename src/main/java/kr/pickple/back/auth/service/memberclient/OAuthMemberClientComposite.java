package kr.pickple.back.auth.service.memberclient;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static kr.pickple.back.auth.exception.AuthExceptionCode.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import kr.pickple.back.auth.domain.oauth.OAuthMember;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import kr.pickple.back.auth.exception.AuthException;

@Component
public class OAuthMemberClientComposite {

    private final Map<OAuthProvider, OAuthMemberClient> mapping;

    public OAuthMemberClientComposite(final Set<OAuthMemberClient> clients) {
        mapping = clients.stream()
                .collect(toMap(
                        OAuthMemberClient::oAuthProvider,
                        identity()
                ));
    }

    public OAuthMember fetch(final OAuthProvider oAuthProvider, final String authCode) {
        return getClient(oAuthProvider).fetch(authCode);
    }

    private OAuthMemberClient getClient(final OAuthProvider oAuthProvider) {
        return Optional.ofNullable(mapping.get(oAuthProvider))
                .orElseThrow(() -> new AuthException(AUTH_NOT_FOUND_OAUTH_PROVIDER, oAuthProvider.toString()));
    }
}
