package kr.pickple.back.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.game.domain.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

}
