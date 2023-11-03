package kr.pickple.back.common.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.pickple.back.common.domain.RegistrationStatus;

@Converter
public final class RegistrationStatusAttributeConverter implements AttributeConverter<RegistrationStatus, String> {

    @Override
    public String convertToDatabaseColumn(final RegistrationStatus registrationStatus) {
        return registrationStatus.getDescription();
    }

    @Override
    public RegistrationStatus convertToEntityAttribute(final String description) {
        return RegistrationStatus.from(description);
    }
}
