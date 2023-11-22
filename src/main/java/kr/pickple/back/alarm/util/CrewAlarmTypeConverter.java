package kr.pickple.back.alarm.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.alarm.domain.CrewAlarmType;

@Convert
public class CrewAlarmTypeConverter implements AttributeConverter<CrewAlarmType, String> {

    @Override
    public String convertToDatabaseColumn(final CrewAlarmType Type) {
        return Type.getDescription();
    }

    @Override
    public CrewAlarmType convertToEntityAttribute(final String status) {
        return CrewAlarmType.from(status);
    }
}
