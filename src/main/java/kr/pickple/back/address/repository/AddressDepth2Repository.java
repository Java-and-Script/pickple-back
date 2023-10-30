package kr.pickple.back.address.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;

public interface AddressDepth2Repository extends JpaRepository<AddressDepth2, Long> {

    List<AddressDepth2> findByAddressDepth1(final AddressDepth1 addressDepth1);
}
