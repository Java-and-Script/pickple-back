package kr.pickple.back.fixture.domain;

import kr.pickple.back.address.repository.entity.AddressDepth1Entity;
import kr.pickple.back.address.repository.entity.AddressDepth2Entity;

public class AddressFixtures {

    public static AddressDepth1Entity addressDepth1Build() {
        return AddressDepth1Entity.builder()
                .name("서울시")
                .build();
    }

    public static AddressDepth2Entity addressDepth2Build() {
        return AddressDepth2Entity.builder()
                .name("영등포구")
                .addressDepth1Id(addressDepth1Build())
                .build();
    }
}
