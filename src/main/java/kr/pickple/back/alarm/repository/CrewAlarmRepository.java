package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.CrewAlarm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewAlarmRepository extends JpaRepository<CrewAlarm, Long> {

    List<CrewAlarm> findByMemberIdAndIdLessThanOrderByIdDesc(final Long memberId, final Long id, final Pageable page);

    boolean existsByMemberIdAndIdLessThan(final Long memberId, final Long id);

    boolean existsByMemberIdAndIsRead(final Long memberId, final AlarmStatus alarmStatus);

    List<CrewAlarm> findByMemberIdOrderByIdDesc(final Long memberId, final Pageable page);
}
