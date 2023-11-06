package kr.pickple.back.crew.repository;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

    Optional<CrewMember> findByMemberIdAndCrewId(final Long memberId, final Long crewId);

    Optional<CrewMember> findByMemberAndCrew(final Member member, final Crew crew);

    void delete(final CrewMember crewMember);
}
