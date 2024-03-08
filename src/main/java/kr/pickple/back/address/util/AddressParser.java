package kr.pickple.back.address.util;

import java.util.Arrays;
import java.util.List;

public final class AddressParser {

    public static final int ADDRESS_DEPTH_SIZE = 2;
    private static final String ADDRESS_DELIMITER_REGEX = "[\\s+]";

    public static List<String> splitToAddressDepth1And2(final String mainAddressName) {
        return Arrays.stream(mainAddressName.split(ADDRESS_DELIMITER_REGEX))
                .limit(ADDRESS_DEPTH_SIZE)
                .map(AddressParser::addCitySuffixToFirstAddress)
                .toList();
    }

    private static String addCitySuffixToFirstAddress(final String firstAddressName) {
        return firstAddressName.equals("서울") ? "서울시" : firstAddressName;
    }
}
