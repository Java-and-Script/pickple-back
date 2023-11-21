package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.GameAlarm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameAlarmRepository extends JpaRepository<GameAlarm, Long> {

    List<GameAlarm> findByMemberIdAndIdLessThanOrderByIdDesc(final Long memberId, final Long id, final Pageable page);

    boolean existsByMemberIdAndIdLessThan(final Long memberId, final Long id);

    boolean existsByMemberIdAndIsRead(final Long memberId, final AlarmStatus alarmStatus);

    List<GameAlarm> findByMemberIdOrderByIdDesc(final Long memberId, final Pageable page);
}
