package kr.pickple.back.address.util;

import java.util.Arrays;
import java.util.List;

public final class AddressParser {

    public static List<String> convertToDepthedAddress(final String mainAddress) {
        return Arrays.stream(mainAddress.split(" "))
                .limit(2)
                .map(AddressParser::addCityToFirstAddress)
                .toList();
    }

    private static String addCityToFirstAddress(String firstAddress) {
        return firstAddress.equals("서울") ? "서울시" : firstAddress;
    }
}
