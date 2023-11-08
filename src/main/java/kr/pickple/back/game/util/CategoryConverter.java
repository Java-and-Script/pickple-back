package kr.pickple.back.game.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import kr.pickple.back.game.domain.Category;

@Component
public class CategoryConverter implements Converter<String, Category> {

    @Override
    public Category convert(final String source) {
        return Category.from(source);
    }
}
