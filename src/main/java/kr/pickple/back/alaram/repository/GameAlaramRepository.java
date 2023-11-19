package kr.pickple.back.alaram.repository;

import kr.pickple.back.alaram.domain.GameAlaram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAlaramRepository extends JpaRepository<GameAlaram, Long> {
}
