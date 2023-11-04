package kr.pickple.back.fixture.setup;

import static kr.pickple.back.address.exception.AddressExceptionCode.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.address.exception.AddressException;
import kr.pickple.back.address.repository.AddressDepth1Repository;
import kr.pickple.back.address.repository.AddressDepth2Repository;

@Component
public class AddressSetup {

    @Autowired
    private AddressDepth1Repository addressDepth1Repository;

    @Autowired
    private AddressDepth2Repository addressDepth2Repository;

    public AddressDepth1 findAddressDepth1(String name) {
        return addressDepth1Repository.findByName(name)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, name));
    }

    public AddressDepth2 findAddressDepth2(String name) {
        return addressDepth2Repository.findByNameAndAddressDepth1(name, findAddressDepth1("서울시"))
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, name));
    }
}
