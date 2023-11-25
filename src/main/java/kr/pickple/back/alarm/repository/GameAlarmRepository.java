package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.GameAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameAlarmRepository extends JpaRepository<GameAlarm, Long> {

    boolean existsByMemberIdAndIsRead(final Long memberId, final AlarmStatus alarmStatus);

    @Query("SELECT ga FROM GameAlarm ga JOIN FETCH ga.game WHERE ga.member.id = :memberId")
    List<GameAlarm> findByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    Optional<GameAlarm> findByMemberIdAndId(final Long memberId, final Long gameAlarmId);
}
