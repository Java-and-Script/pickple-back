package kr.pickple.back.alarm.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.alarm.domain.GameAlarmType;

@Convert
public class GameAlarmTypeConverter implements AttributeConverter<GameAlarmType, String> {

    @Override
    public String convertToDatabaseColumn(final GameAlarmType Type) {
        return Type.getDescription();
    }

    @Override
    public GameAlarmType convertToEntityAttribute(final String status) {
        return GameAlarmType.from(status);
    }
}
