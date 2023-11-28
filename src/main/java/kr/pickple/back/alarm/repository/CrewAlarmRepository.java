package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.CrewAlarm;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewAlarmRepository extends JpaRepository<CrewAlarm, Long> {

    boolean existsByMemberIdAndIsRead(final Long memberId, final Boolean isRead);

    void deleteByMemberId(final Long memberId);

    Optional<CrewAlarm> findByMemberIdAndId(final Long memberId, final Long crewAlarmId);

    @Query("SELECT ca " +
            "FROM CrewAlarm ca LEFT JOIN FETCH ca.crew " +
            "WHERE ca.member.id = :memberId AND ca.id < :cursorId " +
            "ORDER BY ca.createdAt DESC")
    List<CrewAlarm> findByMemberIdAndIdLessThanOrderByCreatedAtDesc(
            @Param("memberId") final Long loggedInMemberId,
            final Long cursorId,
            final PageRequest of
    );

    @Query("SELECT ca " +
            "FROM CrewAlarm ca LEFT JOIN FETCH ca.crew " +
            "WHERE ca.member.id = :memberId " +
            "ORDER BY ca.createdAt DESC")
    List<CrewAlarm> findByMemberIdOrderByCreatedAtDesc(
            @Param("memberId") final Long loggedInMemberId,
            final PageRequest of
    );
}
