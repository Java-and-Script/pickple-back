package kr.pickple.back.auth.util;

import org.springframework.core.convert.converter.Converter;

import kr.pickple.back.auth.domain.oauth.OAuthProvider;

public class OAuthProviderConverter implements Converter<String, OAuthProvider> {

    @Override
    public OAuthProvider convert(final String type) {
        return OAuthProvider.from(type);
    }
}
