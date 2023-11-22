package kr.pickple.back.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    POLYGON("polygon", Integer.MAX_VALUE, 1000);

    private final String cacheName;
    private final int expireAfterWrite;
    private final int maximumSize;
}
