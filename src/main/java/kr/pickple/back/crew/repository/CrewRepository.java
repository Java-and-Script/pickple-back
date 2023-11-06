package kr.pickple.back.crew.repository;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.crew.domain.Crew;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    boolean existsByName(final String name);

    Page<Crew> findByAddressDepth1AndAddressDepth2(final AddressDepth1 addressDepth1, final AddressDepth2 addressDepth2, final Pageable pageable);
}
