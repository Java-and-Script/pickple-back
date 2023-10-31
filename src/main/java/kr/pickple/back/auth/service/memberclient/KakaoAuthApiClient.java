package kr.pickple.back.auth.service.memberclient;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

import kr.pickple.back.auth.dto.kakao.KakaoTokenResponse;

public interface KakaoAuthApiClient {

    @PostExchange
    KakaoTokenResponse fetchToken(@RequestParam final MultiValueMap<String, String> params);
}
