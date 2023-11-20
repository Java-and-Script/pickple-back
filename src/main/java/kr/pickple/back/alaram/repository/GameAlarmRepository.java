package kr.pickple.back.alaram.repository;

import kr.pickple.back.alaram.domain.GameAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAlarmRepository extends JpaRepository<GameAlarm, Long> {
}
