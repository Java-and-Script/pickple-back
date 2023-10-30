package kr.pickple.back.auth.dto.kakao;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.*;
import static lombok.AccessLevel.*;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PRIVATE)
@JsonNaming(SnakeCaseStrategy.class)
public class KakaoTokenResponse {

    private String tokenType;
    private String accessToken;
    private String expiresIn;
}
