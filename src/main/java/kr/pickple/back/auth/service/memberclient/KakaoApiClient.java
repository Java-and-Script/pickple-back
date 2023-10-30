package kr.pickple.back.auth.service.memberclient;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import kr.pickple.back.auth.dto.kakao.KakaoMemberResponse;
import kr.pickple.back.auth.dto.kakao.KakaoTokenResponse;

public interface KakaoApiClient {

    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoTokenResponse fetchToken(@RequestParam final MultiValueMap<String, String> params);

    @GetExchange(url = "https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) final String bearerToken);
}
