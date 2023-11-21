package kr.pickple.back.alarm.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.alarm.domain.AlarmType;

@Convert
public class AlarmTypeConverter implements AttributeConverter<AlarmType, String> {

    @Override
    public String convertToDatabaseColumn(final AlarmType Type) {
        return Type.getDescription();
    }

    @Override
    public AlarmType convertToEntityAttribute(final String Type) {
        return AlarmType.from(Type);
    }
}
