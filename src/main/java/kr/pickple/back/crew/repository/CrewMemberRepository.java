package kr.pickple.back.crew.repository;

import kr.pickple.back.crew.domain.CrewMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

    Optional<CrewMember> findByMemberIdAndCrewId(final Long memberId, final Long crewId);
}
