package kr.pickple.back.address.service.kakao;

import static org.springframework.http.HttpHeaders.*;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import kr.pickple.back.address.dto.kakao.KakaoAddressResponse;

public interface KakaoAddressSearchApiClient {

    @GetExchange
    KakaoAddressResponse fetchAddress(
            @RequestHeader(name = AUTHORIZATION) final String kakaoAK,
            @RequestParam final MultiValueMap<String, String> params
    );
}
