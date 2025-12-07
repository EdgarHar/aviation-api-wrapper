package com.sporty.aviationapiwrapper.service;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.AirportDataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirportService {

    private final AirportDataProvider airportDataProvider;
    private final AirportCacheService cacheService;

    /**
     * Retrieves airport information by ICAO code with caching support.
     * First checks the cache, and if not found, fetches from the provider
     * and caches the result for subsequent requests.
     *
     * @param icaoCode the ICAO airport code (4-letter code)
     * @return Optional containing airport information if found, empty otherwise
     */
    public Optional<AirportInfo> getAirportByIcao(String icaoCode) {
        return cacheService.get(icaoCode)
                .or(() -> fetchAndCache(icaoCode));
    }

    private Optional<AirportInfo> fetchAndCache(String icaoCode) {
        log.debug("Cache miss for ICAO: {} - fetching from provider", icaoCode);
        return airportDataProvider.getAirportByIcaoCode(icaoCode)
                .map(info -> {
                    cacheService.put(icaoCode, info);
                    return info;
                });
    }
}
