package kr.pickple.back.alarm.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.alarm.domain.AlarmStatus;

@Convert
public class AlarmStatusConverter implements AttributeConverter<AlarmStatus, String> {

    @Override
    public String convertToDatabaseColumn(final AlarmStatus status) {
        return status.getDescription();
    }

    @Override
    public AlarmStatus convertToEntityAttribute(final String status) {
        return AlarmStatus.from(status);
    }
}
