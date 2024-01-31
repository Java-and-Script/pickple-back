package kr.pickple.back.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.game.domain.GamePosition;

public interface GamePositionRepository extends JpaRepository<GamePosition, Long> {

}
