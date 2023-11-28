package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.GameAlarm;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameAlarmRepository extends JpaRepository<GameAlarm, Long> {

    boolean existsByMemberIdAndIsRead(final Long memberId, final Boolean isRead);

    void deleteByMemberId(final Long memberId);

    Optional<GameAlarm> findByMemberIdAndId(final Long memberId, final Long gameAlarmId);

    @Query("SELECT ga " +
            "FROM GameAlarm ga LEFT JOIN FETCH ga.game " +
            "WHERE ga.member.id = :memberId " +
            "ORDER BY ga.createdAt DESC")
    List<GameAlarm> findByMemberIdOrderByCreatedAtDesc(
            @Param("memberId") final Long loggedInMemberId,
            final PageRequest of
    );

    @Query("SELECT ga " +
            "FROM GameAlarm ga LEFT JOIN FETCH ga.game " +
            "WHERE ga.member.id = :memberId AND ga.id < :cursorId " +
            "ORDER BY ga.createdAt DESC")
    List<GameAlarm> findByMemberIdAndIdLessThanOrderByCreatedAtDesc(
            @Param("memberId") final Long loggedInMemberId,
            @Param("cursorId") final Long cursorId,
            final PageRequest of
    );
}
