package kr.pickple.back.alaram.util;

import jakarta.persistence.AttributeConverter;
import kr.pickple.back.alaram.domain.AlarmType;

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
