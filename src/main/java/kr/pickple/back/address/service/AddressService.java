package kr.pickple.back.address.service;

import org.springframework.stereotype.Service;

import kr.pickple.back.address.dto.response.AllAddressResponse;
import kr.pickple.back.address.implement.AddressReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressReader addressReader;

    public AllAddressResponse findAllAddress() {
        return addressReader.readAllAddress();
    }
}
