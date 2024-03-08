package kr.pickple.back.fixture.setup;

import static kr.pickple.back.address.exception.AddressExceptionCode.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.address.exception.AddressException;
import kr.pickple.back.address.repository.AddressDepth1Repository;
import kr.pickple.back.address.repository.AddressDepth2Repository;
import kr.pickple.back.address.repository.entity.AddressDepth1Entity;
import kr.pickple.back.address.repository.entity.AddressDepth2Entity;

@Component
public class AddressSetup {

    @Autowired
    private AddressDepth1Repository addressDepth1Repository;

    @Autowired
    private AddressDepth2Repository addressDepth2Repository;

    public AddressDepth1Entity findAddressDepth1(String name) {
        return addressDepth1Repository.findByName(name)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, name));
    }

    public AddressDepth2Entity findAddressDepth2(String name) {
        return addressDepth2Repository.findByNameAndAddressDepth1Id(name, findAddressDepth1("서울시").getId())
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, name));
    }
}
