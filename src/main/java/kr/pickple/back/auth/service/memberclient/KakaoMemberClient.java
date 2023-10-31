package kr.pickple.back.auth.service.memberclient;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import kr.pickple.back.auth.config.property.KakaoOAuthProperties;
import kr.pickple.back.auth.domain.oauth.OAuthMember;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import kr.pickple.back.auth.dto.kakao.KakaoTokenResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoMemberClient implements OAuthMemberClient {

    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final KakaoMemberApiClient kakaoMemberApiClient;
    private final KakaoOAuthProperties kakaoOauthProperties;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public OAuthMember fetch(final String authCode) {
        final KakaoTokenResponse tokenInfo = kakaoAuthApiClient.fetchToken(tokenRequestParams(authCode));

        return kakaoMemberApiClient.fetchMember("Bearer " + tokenInfo.getAccessToken()).toOAuthMember();
    }

    private MultiValueMap<String, String> tokenRequestParams(final String authCode) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOauthProperties.getClientId());
        params.add("redirect_uri", kakaoOauthProperties.getRedirectUrl());
        params.add("code", authCode);
        params.add("client_secret", kakaoOauthProperties.getClientSecret());

        return params;
    }
}
