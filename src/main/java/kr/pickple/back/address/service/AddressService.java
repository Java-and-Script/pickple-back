package kr.pickple.back.address.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.AllAddress;
import kr.pickple.back.address.dto.response.AllAddressResponse;
import kr.pickple.back.address.implement.AddressReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressReader addressReader;

    /**
     * 지역 목록 조회
     */
    public AllAddressResponse findAllAddress() {
        final AllAddress allAddress = addressReader.readAllAddress();

        return AllAddressResponse.builder()
                .addressDepth1(allAddress.getAddressDepth1Name())
                .addressDepth2List(allAddress.getAddressDepth2Names())
                .build();
    }
}
