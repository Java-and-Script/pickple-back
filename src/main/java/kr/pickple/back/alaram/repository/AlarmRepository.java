package kr.pickple.back.alaram.repository;

import kr.pickple.back.alaram.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlaramRepository extends JpaRepository<Alarm,Long> {
}
