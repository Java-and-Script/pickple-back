package kr.pickple.back.auth.service.memberclient;

import kr.pickple.back.auth.domain.oauth.OauthMember;
import kr.pickple.back.auth.domain.oauth.OauthProvider;

public interface OauthMemberClient {

    OauthProvider oauthProvider();

    OauthMember fetch(final String code);
}
