package kr.pickple.back.chat.util;

import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import kr.pickple.back.chat.domain.RoomType;

@Component
public class RoomTypeAttributeConverter implements AttributeConverter<RoomType, String> {

    @Override
    public String convertToDatabaseColumn(final RoomType roomType) {
        return roomType.getDescription();
    }

    @Override
    public RoomType convertToEntityAttribute(final String description) {
        return RoomType.from(description);
    }
}
