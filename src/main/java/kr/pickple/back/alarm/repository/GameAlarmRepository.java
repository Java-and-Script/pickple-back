package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.GameAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAlarmRepository extends JpaRepository<GameAlarm, Long> {
    boolean existsByMemberIdAndIsRead(final Long memberId, final AlarmStatus alarmStatus);
}
