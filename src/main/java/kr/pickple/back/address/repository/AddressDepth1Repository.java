package kr.pickple.back.address.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.address.domain.AddressDepth1;

public interface AddressDepth1Repository extends JpaRepository<AddressDepth1, Long> {

    Optional<AddressDepth1> findByName(final String name);
}
