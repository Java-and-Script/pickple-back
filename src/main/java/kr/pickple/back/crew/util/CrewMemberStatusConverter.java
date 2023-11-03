package kr.pickple.back.crew.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import kr.pickple.back.common.domain.RegistrationStatus;

@Convert

public class CrewMemberStatusConverter implements AttributeConverter<RegistrationStatus, String> {

    @Override
    public String convertToDatabaseColumn(final RegistrationStatus status) {
        return status.getDescription();
    }

    @Override
    public RegistrationStatus convertToEntityAttribute(final String status) {
        return RegistrationStatus.from(status);
    }
}
