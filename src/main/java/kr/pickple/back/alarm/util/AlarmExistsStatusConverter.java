package kr.pickple.back.alarm.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.alarm.domain.AlarmExistsStatus;

@Convert
public class AlarmExistsStatusConverter implements AttributeConverter<AlarmExistsStatus, String> {

    @Override
    public String convertToDatabaseColumn(final AlarmExistsStatus Status) {
        return Status.getDescription();
    }

    @Override
    public AlarmExistsStatus convertToEntityAttribute(final String Status) {
        return AlarmExistsStatus.from(Status);
    }
}
