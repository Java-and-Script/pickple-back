package kr.pickple.back.address.implement;

import static kr.pickple.back.address.exception.AddressExceptionCode.*;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.AllAddress;
import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.address.exception.AddressException;
import kr.pickple.back.address.repository.AddressDepth1Repository;
import kr.pickple.back.address.repository.AddressDepth2Repository;
import kr.pickple.back.address.repository.entity.AddressDepth1Entity;
import kr.pickple.back.address.repository.entity.AddressDepth2Entity;
import kr.pickple.back.address.util.AddressParser;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressReader {

    private final AddressDepth1Repository addressDepth1Repository;
    private final AddressDepth2Repository addressDepth2Repository;

    /**
     * 지역 목록 조회
     */
    @Cacheable(cacheManager = "caffeineCacheManager", cacheNames = "address", key = "'all'")
    public AllAddress readAllAddress() {
        final AddressDepth1Entity addressDepth1Entity = addressDepth1Repository.findAll().get(0);
        final List<String> addressDepth2Names = addressDepth2Repository.findAllByAddressDepth1Id(
                        addressDepth1Entity.getId())
                .stream()
                .map(AddressDepth2Entity::getName)
                .toList();

        return AddressMapper.mapToAllAddressDomain(addressDepth1Entity.getName(), addressDepth2Names);
    }

    /**
     * 주소1과 주소2의 ID를 통해 MainAddress 조회
     */
    public MainAddress readMainAddressByIds(final Long addressDepth1Id, final Long addressDepth2Id) {
        final AddressDepth1Entity addressDepth1Entity = addressDepth1Repository.findById(addressDepth1Id)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1Id));

        final AddressDepth2Entity addressDepth2Entity = addressDepth2Repository.findById(addressDepth2Id)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth2Id));

        return AddressMapper.mapToMainAddressDomain(
                AddressMapper.mapAddressEntityToDomain(addressDepth1Entity),
                AddressMapper.mapAddressEntityToDomain(addressDepth2Entity)
        );
    }

    /**
     * 주소1과 주소2의 이름을 통해 MainAddress 조회
     */
    public MainAddress readMainAddressByNames(final String addressDepth1Name, final String addressDepth2Name) {
        final AddressDepth1Entity addressDepth1Entity = addressDepth1Repository.findByName(addressDepth1Name)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1Name));

        final AddressDepth2Entity addressDepth2Entity = addressDepth2Repository.findByNameAndAddressDepth1Id(
                        addressDepth2Name, addressDepth1Entity.getId())
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1Name, addressDepth2Name));

        return AddressMapper.mapToMainAddressDomain(
                AddressMapper.mapAddressEntityToDomain(addressDepth1Entity),
                AddressMapper.mapAddressEntityToDomain(addressDepth2Entity)
        );
    }

    /**
     * 전체 주소를 주소1과 주소2로 나누고, 이를 이용해 MainAddress 조회
     */
    public MainAddress readMainAddressFromFullAddress(final String mainAddressName) {
        final List<String> addressDepth1And2Names = AddressParser.splitToAddressDepth1And2(mainAddressName);
        final String addressDepth1Name = addressDepth1And2Names.get(0);
        final String addressDepth2Name = addressDepth1And2Names.get(1);

        return readMainAddressByNames(addressDepth1Name, addressDepth2Name);
    }
}
