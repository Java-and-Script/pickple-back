package kr.pickple.back.address.dto.response;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MainAddress {

    private AddressDepth1 addressDepth1;
    private AddressDepth2 addressDepth2;
}
