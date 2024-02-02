package kr.pickple.back.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.game.domain.GamePosition;

public interface GamePositionRepository extends JpaRepository<GamePosition, Long> {

    List<GamePosition> findAllByGameId(final Long gameId);
}

