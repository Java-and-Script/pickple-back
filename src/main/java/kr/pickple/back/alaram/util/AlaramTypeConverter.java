package kr.pickple.back.alaram.util;

import jakarta.persistence.AttributeConverter;
import kr.pickple.back.alaram.domain.AlaramType;

public class AlaramTypeConverter implements AttributeConverter<AlaramType, String> {

    @Override
    public String convertToDatabaseColumn(final AlaramType Type) {
        return Type.getDescription();
    }

    @Override
    public AlaramType convertToEntityAttribute(final String Type) {
        return AlaramType.from(Type);
    }
}
