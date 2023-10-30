package kr.pickple.back.address.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.address.dto.response.AllAddressResponse;
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
}
