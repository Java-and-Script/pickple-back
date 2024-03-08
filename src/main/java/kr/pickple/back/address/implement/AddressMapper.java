package kr.pickple.back.address.implement;

import java.util.List;

import kr.pickple.back.address.domain.Address;
import kr.pickple.back.address.domain.AllAddress;
import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.address.repository.entity.AddressEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddressMapper {

    public static AllAddress mapToAllAddressDomain(
            final String addressDepth1Name,
            final List<String> addressDepth2Names
    ) {
        return AllAddress.builder()
                .addressDepth1Name(addressDepth1Name)
                .addressDepth2Names(addressDepth2Names)
                .build();
    }

    public static MainAddress mapToMainAddressDomain(final Address addressDepth1, final Address addressDepth2) {
        return MainAddress.builder()
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .build();
    }

    public static Address mapAddressEntityToDomain(final AddressEntity addressEntity) {
        return Address.builder()
                .id(addressEntity.getId())
                .name(addressEntity.getName())
                .build();
    }
}
