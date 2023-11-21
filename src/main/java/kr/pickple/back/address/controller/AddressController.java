package kr.pickple.back.address.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.pickple.back.address.dto.response.AllAddressResponse;
import kr.pickple.back.address.service.AddressService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public AllAddressResponse findAllAddress() {
        return addressService.findAllAddress();
    }
}
