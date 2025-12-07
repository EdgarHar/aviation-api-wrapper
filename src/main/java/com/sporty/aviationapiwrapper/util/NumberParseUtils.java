package com.sporty.aviationapiwrapper.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberParseUtils {

    public static Double parseDouble(String value) {
        return Optional.ofNullable(value)
                .filter(NumberUtils::isCreatable)
                .map(Double::parseDouble)
                .orElse(null);
    }

    public static Integer parseInteger(String value) {
        return Optional.ofNullable(value)
                .filter(NumberUtils::isCreatable)
                .map(Double::parseDouble)
                .map(Double::intValue)
                .orElse(null);
    }
}