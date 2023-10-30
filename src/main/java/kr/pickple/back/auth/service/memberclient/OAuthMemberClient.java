package kr.pickple.back.auth.service.memberclient;

import kr.pickple.back.auth.domain.oauth.OAuthMember;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;

public interface OAuthMemberClient {

    OAuthProvider oAuthProvider();

    OAuthMember fetch(final String code);
}
