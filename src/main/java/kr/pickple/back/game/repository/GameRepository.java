package kr.pickple.back.game.repository;

import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.exception.GameException;

public interface GameRepository extends JpaRepository<Game, Long>, GameSearchRepository {

    Page<Game> findByAddressDepth1IdAndAddressDepth2IdAndStatusNot(
            final Long addressDepth1Id,
            final Long addressDepth2Id,
            final GameStatus status,
            final Pageable pageable
    );

    Optional<Game> findByChatRoomId(final Long chatRoomId);

    default Game getGameById(final Long gameId) {
        return findById(gameId).orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
    }
}
