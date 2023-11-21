package kr.pickple.back.game.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.pickple.back.game.domain.GameStatus;

@Converter
public final class GameStatusConverter implements AttributeConverter<GameStatus, String> {

    @Override
    public String convertToDatabaseColumn(final GameStatus gameStatus) {
        return gameStatus.getDescription();
    }

    @Override
    public GameStatus convertToEntityAttribute(final String description) {
        return GameStatus.from(description);
    }
}
