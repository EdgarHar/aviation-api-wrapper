package com.sporty.aviationapiwrapper.utils;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeignClientExecuteUtils {

    public static <T> Optional<T> execute(
            String providerName,
            String icaoCode,
            Supplier<T> feignCall) {
        try {
            return Optional.ofNullable(feignCall.get());
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                log.warn("[{}] HTTP {} (client error) for ICAO: {} - not retrying", providerName, e.status(), icaoCode);
                throw new IllegalArgumentException("Client error: HTTP " + e.status(), e);
            }
            log.warn("[{}] HTTP {} for ICAO: {} - will retry", providerName, e.status(), icaoCode);
            throw e;
        }
    }
}