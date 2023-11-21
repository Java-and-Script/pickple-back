package kr.pickple.back.address.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AllAddressResponse {

    private String addressDepth1;
    private List<String> addressDepth2List;
}
