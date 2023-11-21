package kr.pickple.back.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;

public interface AddressDepth2Repository extends JpaRepository<AddressDepth2, Long> {

    List<AddressDepth2> findByAddressDepth1(final AddressDepth1 addressDepth1);

    Optional<AddressDepth2> findByNameAndAddressDepth1(final String name, final AddressDepth1 addressDepth1);
}
