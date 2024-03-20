package kr.pickple.back.crew.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;

public interface CrewMemberRepository extends JpaRepository<CrewMemberEntity, Long> {

    Optional<CrewMemberEntity> findByMemberIdAndCrewId(final Long memberId, final Long crewId);

    List<CrewMemberEntity> findAllByCrewIdAndStatus(final Long crewId, final RegistrationStatus status);

    List<CrewMemberEntity> findAllByMemberIdAndStatus(final Long memberId, final RegistrationStatus status);

    Boolean existsByCrewIdAndMemberId(final Long crewId, final Long memberId);

    Boolean existsByCrewIdAndMemberIdAndStatus(final Long crewId, final Long memberId, final RegistrationStatus status);

    @Modifying(clearAutomatically = true)
    @Query("update CrewMemberEntity cm set cm.status = :status where cm.id = :crewMemberId")
    void updateRegistrationStatus(final Long crewMemberId, final RegistrationStatus status);
}
