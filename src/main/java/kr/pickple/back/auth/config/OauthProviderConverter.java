package kr.pickple.back.auth.config;

import org.springframework.core.convert.converter.Converter;

import kr.pickple.back.auth.domain.oauth.OauthProvider;

public class OauthProviderConverter implements Converter<String, OauthProvider> {

    @Override
    public OauthProvider convert(final String type) {
        return OauthProvider.from(type);
    }
}
