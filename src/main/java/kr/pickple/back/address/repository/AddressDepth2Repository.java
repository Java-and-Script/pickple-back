package kr.pickple.back.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.address.repository.entity.AddressDepth2Entity;

public interface AddressDepth2Repository extends JpaRepository<AddressDepth2Entity, Long> {

    List<AddressDepth2Entity> findAllByAddressDepth1Id(final Long addressDepth1Id);

    Optional<AddressDepth2Entity> findByNameAndAddressDepth1Id(final String name, final Long addressDepth1Id);
}
