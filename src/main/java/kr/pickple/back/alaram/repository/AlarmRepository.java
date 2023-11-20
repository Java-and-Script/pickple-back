package kr.pickple.back.alaram.repository;

import kr.pickple.back.alaram.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
