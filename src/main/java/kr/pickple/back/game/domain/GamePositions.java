package kr.pickple.back.game.domain;

import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.position.domain.Position;

@Embeddable
public class GamePositions {

    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "game", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<GamePosition> gamePositions = new ArrayList<>();

    public List<Position> getPositions() {
        return gamePositions.stream()
                .map(GamePosition::getPosition)
                .toList();
    }

    public void updateGamePositions(final Game game, final List<Position> positions) {
        validateIsDuplicatedPositions(positions);

        positions.stream()
                .map(position -> buildGamePosition(game, position))
                .forEach(gamePositions::add);
    }

    private void validateIsDuplicatedPositions(final List<Position> positions) {
        long distinctPositionsSize = positions.stream()
                .distinct()
                .count();

        if (distinctPositionsSize < positions.size()) {
            throw new GameException(GAME_POSITIONS_IS_DUPLICATED, positions);
        }
    }

    private GamePosition buildGamePosition(final Game game, final Position position) {
        return GamePosition.builder()
                .position(position)
                .game(game)
                .build();
    }
}
