package kr.pickple.back.position.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.pickple.back.position.domain.Position;

@Converter
public final class PositionConverter implements AttributeConverter<Position, String> {

	@Override
	public String convertToDatabaseColumn(final Position position) {
		return position.getAcronym();
	}

	@Override
	public Position convertToEntityAttribute(final String acronym) {
		return Position.fromGamePositions(acronym);
	}
}
