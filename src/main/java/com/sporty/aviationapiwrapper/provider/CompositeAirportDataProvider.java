package com.sporty.aviationapiwrapper.provider;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class CompositeAirportDataProvider implements AirportDataProvider {

    private final List<AirportDataProvider> providers;

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
            log.debug("Trying provider: {}", provider.getClass().getSimpleName());
            return provider.getAirportByIcaoCode(icaoCode);
        } catch (Exception e) {
            log.debug("Provider {} failed for ICAO {}: {}", provider.getClass().getSimpleName(), icaoCode, e.getMessage());
            return Optional.empty();
        }
    }
}