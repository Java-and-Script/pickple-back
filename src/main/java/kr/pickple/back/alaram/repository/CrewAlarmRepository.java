package kr.pickple.back.alaram.repository;

import kr.pickple.back.alaram.domain.CrewAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewAlaramRepository extends JpaRepository<CrewAlarm, Long> {
}
