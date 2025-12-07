package com.sporty.aviationapiwrapper.provider;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Composite provider that implements failover pattern by chaining multiple airport data providers.
 * Attempts each provider in sequence until one successfully returns data or all fail.
 * Exceptions from individual providers are caught and logged without propagating.
 */
@RequiredArgsConstructor
@Slf4j
public class CompositeAirportDataProvider implements AirportDataProvider {

    private final List<AirportDataProvider> providers;

    /**
     * Attempts to retrieve airport information by trying each provider in order.
     * Returns the first successful result, or empty if all providers fail.
     *
     * @param icaoCode the ICAO airport code to lookup
     * @return Optional containing airport information from the first successful provider, empty if all fail
     */
    @Override
    public Optional<AirportInfo> getAirportByIcaoCode(String icaoCode) {
        return providers.stream()
                .map(provider -> tryProvider(provider, icaoCode))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private Optional<AirportInfo> tryProvider(AirportDataProvider provider, String icaoCode) {
        try {
            return provider.getAirportByIcaoCode(icaoCode);
        } catch (Exception e) {
            log.warn("Provider {} failed for ICAO {}: {}", provider.getClass().getSimpleName(), icaoCode, e.getMessage());
            return Optional.empty();
        }
    }
}