package kr.pickple.back.crew.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.crew.domain.CrewStatus;

@Convert
public final class CrewStatusConverter implements AttributeConverter<CrewStatus, String> {

    @Override
    public String convertToDatabaseColumn(final CrewStatus status) {
        return status.getDescription();
    }

    @Override
    public CrewStatus convertToEntityAttribute(final String status) {
        return CrewStatus.from(status);
    }
}
