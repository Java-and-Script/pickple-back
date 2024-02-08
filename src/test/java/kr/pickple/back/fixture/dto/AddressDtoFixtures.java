package kr.pickple.back.fixture.dto;

import java.util.List;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.address.dto.response.AllAddressResponse;
import kr.pickple.back.address.dto.response.MainAddress;

public class AddressDtoFixtures {

    public static AllAddressResponse allAddressResponseBuild(
            final AddressDepth1 addressDepth1,
            final List<AddressDepth2> addressDepth2s
    ) {
        return AllAddressResponse.builder()
                .addressDepth1(addressDepth1.getName())
                .addressDepth2List(addressDepth2s.stream().map(AddressDepth2::getName).toList())
                .build();
    }

    public static MainAddress mainAddressBuild(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2
    ) {
        return MainAddress.builder()
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .build();
    }
}
