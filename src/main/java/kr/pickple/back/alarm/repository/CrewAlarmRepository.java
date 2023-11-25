package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.CrewAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrewAlarmRepository extends JpaRepository<CrewAlarm, Long> {

    boolean existsByMemberIdAndIsRead(final Long memberId, final AlarmStatus alarmStatus);

    List<CrewAlarm> findByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    Optional<CrewAlarm> findByMemberIdAndId(final Long memberId, final Long crewAlarmId);
}
