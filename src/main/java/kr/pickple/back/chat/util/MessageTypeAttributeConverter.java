package kr.pickple.back.chat.util;

import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import kr.pickple.back.chat.domain.MessageType;

@Component
public class MessageTypeAttributeConverter implements AttributeConverter<MessageType, String> {

    @Override
    public String convertToDatabaseColumn(final MessageType messageType) {
        return messageType.getDescription();
    }

    @Override
    public MessageType convertToEntityAttribute(final String description) {
        return MessageType.from(description);
    }
}
