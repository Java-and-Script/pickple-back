package kr.pickple.back.alaram.repository;

import kr.pickple.back.alaram.domain.Alaram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlaramRepository extends JpaRepository<Alaram,Long> {
}
