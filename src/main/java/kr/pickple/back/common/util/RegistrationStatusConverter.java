package kr.pickple.back.common.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import kr.pickple.back.common.domain.RegistrationStatus;

@Component
public class RegistrationStatusConverter implements Converter<String, RegistrationStatus> {

    @Override
    public RegistrationStatus convert(final String source) {
        return RegistrationStatus.from(source);
    }
}
