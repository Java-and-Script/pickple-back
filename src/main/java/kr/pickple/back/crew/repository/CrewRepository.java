package kr.pickple.back.crew.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.crew.repository.entity.CrewEntity;

public interface CrewRepository extends JpaRepository<CrewEntity, Long> {

    Boolean existsByName(final String name);

    Page<CrewEntity> findByAddressDepth1IdAndAddressDepth2Id(
            final Long addressDepth1Id,
            final Long addressDepth2Id,
            final Pageable pageable
    );

    Optional<CrewEntity> findByChatRoomId(final Long chatRoomId);

    List<CrewEntity> findAllByLeaderId(final Long leaderId);

    Integer countByLeaderId(final Long leaderId);

    @Modifying(clearAutomatically = true)
    @Query("update CrewEntity c set c.memberCount = :memberCount, c.status = :status where c.id = :crewId")
    void updateMemberCountAndStatus(final Long crewId, final Integer memberCount, final CrewStatus status);

    @Query("select c.chatRoomId from CrewEntity c where c.id = :crewId")
    Long findChatRoomId(final Long crewId);
}
