package kr.pickple.back.address.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MainAddressResponse {

    private String addressDepth1Name;
    private String addressDepth2Name;
}
