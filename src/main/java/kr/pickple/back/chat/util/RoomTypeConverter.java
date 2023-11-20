package kr.pickple.back.chat.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import kr.pickple.back.chat.domain.RoomType;

@Component
public class RoomTypeConverter implements Converter<String, RoomType> {

    @Override
    public RoomType convert(final String description) {
        return RoomType.from(description);
    }
}
