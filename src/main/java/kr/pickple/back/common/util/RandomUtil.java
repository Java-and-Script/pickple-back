package kr.pickple.back.common.util;

import java.util.Random;

public final class RandomUtil {

    public static Integer getRandomNumber(final int start, final int end) {
        final Random random = new Random();

        return random.nextInt(end) + start;
    }
}
