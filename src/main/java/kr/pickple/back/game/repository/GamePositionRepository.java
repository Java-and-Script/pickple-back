package kr.pickple.back.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.game.repository.entity.GamePositionEntity;

public interface GamePositionRepository extends JpaRepository<GamePositionEntity, Long> {

    List<GamePositionEntity> findAllByGameId(final Long gameId);
}

