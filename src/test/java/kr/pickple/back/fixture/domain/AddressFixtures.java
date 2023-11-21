package kr.pickple.back.fixture.domain;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;

public class AddressFixtures {

    public static AddressDepth1 addressDepth1Build() {
        return AddressDepth1.builder()
                .name("서울시")
                .build();
    }

    public static AddressDepth2 addressDepth2Build() {
        return AddressDepth2.builder()
                .name("영등포구")
                .addressDepth1(addressDepth1Build())
                .build();
    }
}
