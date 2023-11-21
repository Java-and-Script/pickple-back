package kr.pickple.back.auth.service.memberclient;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static kr.pickple.back.auth.exception.AuthExceptionCode.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import kr.pickple.back.auth.domain.oauth.OauthMember;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.exception.AuthException;

@Component
public class OauthMemberClientComposite {

    private final Map<OauthProvider, OauthMemberClient> mapping;

    public OauthMemberClientComposite(final Set<OauthMemberClient> clients) {
        mapping = clients.stream()
                .collect(toMap(OauthMemberClient::oauthProvider, identity()));
    }

    public OauthMember fetch(final OauthProvider oauthProvider, final String authCode) {
        return getClient(oauthProvider).fetch(authCode);
    }

    private OauthMemberClient getClient(final OauthProvider oauthProvider) {
        return Optional.ofNullable(mapping.get(oauthProvider))
                .orElseThrow(() -> new AuthException(AUTH_NOT_FOUND_OAUTH_PROVIDER, oauthProvider.toString()));
    }
}
