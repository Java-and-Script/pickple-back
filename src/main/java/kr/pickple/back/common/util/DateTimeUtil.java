package kr.pickple.back.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    public static boolean isAfterThanNow(final LocalDate date, final LocalTime time) {
        final LocalDateTime datetime = LocalDateTime.of(date, time);
        final LocalDateTime now = LocalDateTime.now();

        return datetime.isAfter(now);
    }

    public static boolean isEqualOrAfter(final LocalDateTime baseDateTime, final LocalDateTime targetDateTime) {
        return targetDateTime.isEqual(baseDateTime) || targetDateTime.isAfter(baseDateTime);
    }
}
