package kr.pickple.back.crew.repository;

import kr.pickple.back.crew.domain.Crew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    boolean existsByName(String name);

    Optional<Crew> findCrewById(Long id);
}
