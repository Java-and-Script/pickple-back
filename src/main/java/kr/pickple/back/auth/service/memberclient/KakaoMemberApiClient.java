package kr.pickple.back.auth.service.memberclient;

import static org.springframework.http.HttpHeaders.*;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

import kr.pickple.back.auth.dto.kakao.KakaoMemberResponse;

public interface KakaoMemberApiClient {

    @GetExchange
    KakaoMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) final String bearerToken);
}
