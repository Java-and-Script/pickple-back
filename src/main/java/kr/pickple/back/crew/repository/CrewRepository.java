package kr.pickple.back.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.pickple.back.crew.domain.Crew;

@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {

    boolean existsByName(String name);
}
