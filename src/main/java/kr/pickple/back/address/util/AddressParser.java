package kr.pickple.back.address.util;

import java.util.Arrays;
import java.util.List;

public final class AddressParser {

    public static final int ADDRESS_DEPTH_SIZE = 2;

    public static List<String> splitToAddressDepth1And2(final String mainAddress) {
        return Arrays.stream(mainAddress.split(" "))
                .limit(ADDRESS_DEPTH_SIZE)
                .map(AddressParser::addCitySuffixToFirstAddress)
                .toList();
    }

    private static String addCitySuffixToFirstAddress(final String addressDepth1) {
        return addressDepth1.equals("서울") ? "서울시" : addressDepth1;
    }
}
