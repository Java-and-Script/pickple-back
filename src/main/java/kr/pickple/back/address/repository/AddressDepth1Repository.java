package kr.pickple.back.address.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.address.repository.entity.AddressDepth1Entity;

public interface AddressDepth1Repository extends JpaRepository<AddressDepth1Entity, Long> {

    Optional<AddressDepth1Entity> findByName(final String name);
}
