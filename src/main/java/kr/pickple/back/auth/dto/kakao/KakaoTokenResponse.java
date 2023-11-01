package kr.pickple.back.auth.dto.kakao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoTokenResponse {

    private String refreshToken;
    private String tokenType;
    private String accessToken;
    private String expiresIn;
}
