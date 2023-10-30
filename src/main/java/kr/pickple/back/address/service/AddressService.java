package kr.pickple.back.address.service;

import static kr.pickple.back.address.exception.AddressExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.address.dto.response.AllAddressResponse;
import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.exception.AddressException;
import kr.pickple.back.address.repository.AddressDepth1Repository;
import kr.pickple.back.address.repository.AddressDepth2Repository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressDepth1Repository addressDepth1Repository;
    private final AddressDepth2Repository addressDepth2Repository;

    public AllAddressResponse findAllAddress() {
        AddressDepth1 addressDepth1 = addressDepth1Repository.findAll()
                .get(0);

        List<String> addressDepth2List = addressDepth2Repository.findByAddressDepth1(addressDepth1)
                .stream()
                .map(AddressDepth2::getName)
                .toList();

        return AllAddressResponse.builder()
                .addressDepth1(addressDepth1.getName())
                .addressDepth2List(addressDepth2List)
                .build();
    }

    public MainAddressResponse findMainAddressByNames(final String addressDepth1Name, final String addressDepth2Name) {
        AddressDepth1 addressDepth1 = addressDepth1Repository.findByName(addressDepth1Name)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1Name));

        AddressDepth2 addressDepth2 = addressDepth2Repository.findByNameAndAddressDepth1(addressDepth2Name,
                        addressDepth1)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1.getName(), addressDepth2Name));

        return MainAddressResponse.builder()
                .addressDepth1Name(addressDepth1.getName())
                .addressDepth2Name(addressDepth2.getName())
                .build();
    }
}