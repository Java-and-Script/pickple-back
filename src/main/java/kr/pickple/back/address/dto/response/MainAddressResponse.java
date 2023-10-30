package kr.pickple.back.address.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MainAddressResponse {

    private String addressDepth1Name;
    private String addressDepth2Name;
}
