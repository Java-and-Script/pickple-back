package kr.pickple.back.alaram.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.alaram.domain.AlaramStatus;

@Convert
public class AlaramStatusConverter implements AttributeConverter<AlaramStatus,String> {

    @Override
    public String convertToDatabaseColumn(final AlaramStatus status) {
        return status.getDescription();
    }

    @Override
    public AlaramStatus convertToEntityAttribute(final String status) {
        return AlaramStatus.from(status);
    }
}
