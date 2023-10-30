package kr.pickple.back.address.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllAddressResponse {

    private String addressDepth1;
    private List<String> addressDepth2List;
}
