package kr.pickple.back.common.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.pickple.back.common.domain.RegistrationStatus;

@Converter
public final class RegistrationStatusConverter implements AttributeConverter<RegistrationStatus, String> {

    @Override
    public String convertToDatabaseColumn(RegistrationStatus registrationStatus) {
        return registrationStatus.getDescription();
    }

    @Override
    public RegistrationStatus convertToEntityAttribute(String description) {
        return RegistrationStatus.from(description);
    }
}
