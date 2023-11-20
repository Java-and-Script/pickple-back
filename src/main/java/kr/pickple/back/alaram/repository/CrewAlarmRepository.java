package kr.pickple.back.alaram.repository;

import kr.pickple.back.alaram.domain.AlarmStatus;
import kr.pickple.back.alaram.domain.CrewAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewAlarmRepository extends JpaRepository<CrewAlarm, Long> {
    boolean existsByMemberIdAndIsRead(final Long memberId, final AlarmStatus alarmStatus);
}
