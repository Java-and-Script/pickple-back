package kr.pickple.back.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.game.domain.GamePosition;

import java.util.List;

public interface GamePositionRepository extends JpaRepository<GamePosition, Long> {

	List<GamePosition> findAllByGameId(final Long gameId);
}

