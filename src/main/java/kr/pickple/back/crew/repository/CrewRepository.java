package kr.pickple.back.crew.repository;

import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.crew.exception.CrewException;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    Boolean existsByName(final String name);

    Page<Crew> findByAddressDepth1IdAndAddressDepth2Id(
            final Long addressDepth1Id,
            final Long addressDepth2Id,
            final Pageable pageable
    );

    Optional<Crew> findByChatRoomId(final Long chatRoomId);

    List<Crew> findAllByLeaderId(final Long leaderId);

    Integer countByLeaderId(final Long leaderId);

    @Query("update Crew c set c.memberCount = :memberCount, c.status =: status where c.id = :crewId")
    void updateMemberCountAndStatus(final Long crewId, final Integer memberCount, final CrewStatus status);

    default Crew getCrewById(final Long crewId) {
        return findById(crewId).orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }
}
