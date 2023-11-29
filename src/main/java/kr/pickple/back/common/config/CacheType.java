package kr.pickple.back.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    POLYGON("polygon", Integer.MAX_VALUE, 1000),
    ADDRESS("address", Integer.MAX_VALUE, 1000),
    RANKING("ranking", Constants.SIX_HOURS_IN_SECONDS, 10),
    ;

    private final String cacheName;
    private final int expireAfterWrite;
    private final int maximumSize;

    private static class Constants {

        public static final int SIX_HOURS_IN_SECONDS = 60 * 60 * 6;
    }
}
