package kr.pickple.back.crew.repository;

import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.crew.exception.CrewException;

public interface CrewMemberRepository extends JpaRepository<CrewMemberEntity, Long> {

    Optional<CrewMemberEntity> findByMemberIdAndCrewId(final Long memberId, final Long crewId);

    List<CrewMemberEntity> findAllByCrewIdAndStatus(final Long crewId, final RegistrationStatus status);

    List<CrewMemberEntity> findAllByMemberIdAndStatus(final Long memberId, final RegistrationStatus status);

    Boolean existsByCrewIdAndMemberId(final Long crewId, final Long memberId);

    Boolean existsByCrewIdAndMemberIdAndStatus(final Long crewId, final Long memberId, final RegistrationStatus status);

    @Query("update CrewMemberEntity cm set cm.status = :status where cm.id = :crewMemberId")
    void updateRegistrationStatus(final Long crewMemberId, final RegistrationStatus status);

    default CrewMemberEntity getCrewMemberByCrewIdAndMemberId(final Long memberId, final Long crewId) {
        return findByMemberIdAndCrewId(memberId, crewId).orElseThrow(
                () -> new CrewException(CREW_MEMBER_NOT_FOUND, memberId, crewId));
    }
}
