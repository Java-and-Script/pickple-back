package kr.pickple.back.address.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MainAddress {

    private Address addressDepth1;
    private Address addressDepth2;

    public Long getAddressDepth1Id() {
        return this.addressDepth1.getId();
    }

    public String getAddressDepth1Name() {
        return this.addressDepth1.getName();
    }

    public Long getAddressDepth2Id() {
        return this.addressDepth2.getId();
    }

    public String getAddressDepth2Name() {
        return this.addressDepth2.getName();
    }
}
