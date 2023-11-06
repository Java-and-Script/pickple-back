package kr.pickple.back.address.service;

import static kr.pickple.back.address.exception.AddressExceptionCode.*;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.address.dto.response.AllAddressResponse;
import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.exception.AddressException;
import kr.pickple.back.address.repository.AddressDepth1Repository;
import kr.pickple.back.address.repository.AddressDepth2Repository;
import kr.pickple.back.address.util.AddressParser;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressDepth1Repository addressDepth1Repository;
    private final AddressDepth2Repository addressDepth2Repository;

    @Cacheable(cacheNames = "address", key = "'all'")
    public AllAddressResponse findAllAddress() {
        final AddressDepth1 addressDepth1 = addressDepth1Repository.findAll()
                .get(0);

        final List<String> addressDepth2List = addressDepth2Repository.findByAddressDepth1(addressDepth1)
                .stream()
                .map(AddressDepth2::getName)
                .toList();

        return AllAddressResponse.builder()
                .addressDepth1(addressDepth1.getName())
                .addressDepth2List(addressDepth2List)
                .build();
    }

    public MainAddressResponse findMainAddressByNames(final String addressDepth1Name, final String addressDepth2Name) {
        final AddressDepth1 addressDepth1 = addressDepth1Repository.findByName(addressDepth1Name)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1Name));

        final AddressDepth2 addressDepth2 = addressDepth2Repository.findByNameAndAddressDepth1(addressDepth2Name,
                        addressDepth1)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1Name, addressDepth2Name));

        return MainAddressResponse.builder()
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .build();
    }

    //기존 메서드 네이밍 및 시그니처 유지를 위해 임시적으로 아래와 구현했습니다.
    //todo 현호:서로 다른 입력을 받아 MainAddressResponse를 반환하는 두 메서드를 어떻게 통합하면 좋을 지 논의해보면 좋겠습니다.
    public MainAddressResponse findMainAddressByAddressStrings(final String mainAddress) {
        final List<String> depthedAddress = AddressParser.splitToAddressDepth1And2(mainAddress);

        return findMainAddressByNames(depthedAddress.get(0), depthedAddress.get(1));
    }
}
