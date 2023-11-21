package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.CrewAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewAlarmRepository extends JpaRepository<CrewAlarm, Long> {
    boolean existsByMemberIdAndIsRead(final Long memberId, final AlarmStatus alarmStatus);
}
