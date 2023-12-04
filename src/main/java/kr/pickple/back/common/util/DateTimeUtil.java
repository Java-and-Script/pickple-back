package kr.pickple.back.common.util;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    public static boolean isAfterThanNow(final LocalDateTime datetime) {
        return datetime.isAfter(LocalDateTime.now());
    }

    public static boolean isEqualOrAfter(final LocalDateTime baseDateTime, final LocalDateTime targetDateTime) {
        return targetDateTime.isEqual(baseDateTime) || targetDateTime.isAfter(baseDateTime);
    }
}
