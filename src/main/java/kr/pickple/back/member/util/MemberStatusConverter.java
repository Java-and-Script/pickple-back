package kr.pickple.back.member.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.pickple.back.member.domain.MemberStatus;

@Converter
public final class MemberStatusConverter implements AttributeConverter<MemberStatus, String> {

    @Override
    public String convertToDatabaseColumn(final MemberStatus memberStatus) {
        return memberStatus.getDescription();
    }

    @Override
    public MemberStatus convertToEntityAttribute(final String description) {
        return MemberStatus.from(description);
    }
}
