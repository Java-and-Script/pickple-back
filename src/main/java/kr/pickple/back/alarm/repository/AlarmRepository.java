package kr.pickple.back.alarm.repository;

import kr.pickple.back.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Boolean existsByIdLessThan(final Long id);
}
