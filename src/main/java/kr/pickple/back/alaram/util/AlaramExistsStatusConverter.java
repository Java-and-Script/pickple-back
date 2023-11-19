package kr.pickple.back.alaram.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.alaram.domain.AlaramExistsStatus;

@Convert
public class AlaramExistsStatusConverter implements AttributeConverter<AlaramExistsStatus, String> {

    @Override
    public String convertToDatabaseColumn(final AlaramExistsStatus Status) {
        return Status.getDescription();
    }

    @Override
    public AlaramExistsStatus convertToEntityAttribute(final String Status) {
        return AlaramExistsStatus.from(Status);
    }
}
