package kr.pickple.back.crew.repository;

import kr.pickple.back.crew.domain.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    boolean existsByName(final String name);
}
