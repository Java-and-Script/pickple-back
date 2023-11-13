package kr.pickple.back.address.service.kakao;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import kr.pickple.back.address.dto.kakao.Coordinate;
import kr.pickple.back.address.dto.kakao.KakaoAddressResponse;
import kr.pickple.back.auth.config.property.KakaoOauthProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoAddressSearchClient {

    private final KakaoAddressSearchApiClient kakaoAddressSearchApiClient;
    private final KakaoOauthProperties kakaoOauthProperties;

    public Coordinate fetchAddress(final String address) {
        final KakaoAddressResponse kakaoAddressResponse = kakaoAddressSearchApiClient.fetchAddress(
                "KakaoAK " + kakaoOauthProperties.getClientId(),
                addressRequestParams(address)
        );

        return kakaoAddressResponse.toCoordinate();
    }

    private MultiValueMap<String, String> addressRequestParams(final String address) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("query", address);

        return params;
    }
}
