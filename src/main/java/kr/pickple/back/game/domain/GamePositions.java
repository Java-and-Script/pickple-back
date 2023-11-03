package kr.pickple.back.game.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.position.domain.Position;

@Embeddable
public class GamePositions {

    @OneToMany(mappedBy = "game", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<GamePosition> gamePositions = new ArrayList<>();

    public List<Position> getPositions() {
        return gamePositions.stream()
                .map(GamePosition::getPosition)
                .toList();
    }

    public void updateGamePositions(final Game game, final List<Position> positions) {
        positions.stream()
                .distinct()
                .map(position -> buildGamePosition(game, position))
                .forEach(gamePositions::add);
    }

    private GamePosition buildGamePosition(final Game game, final Position position) {
        return GamePosition.builder()
                .position(position)
                .game(game)
                .build();
    }
}
