package com.sporty.aviationapiwrapper.provider;

import com.sporty.aviationapiwrapper.dto.AirportInfo;

import java.util.Optional;

/**
 * Provider interface for fetching airport information from various data sources.
 * Implementations should handle their own error handling and resilience patterns.
 */
public interface AirportDataProvider {

    /**
     * Retrieves airport information by ICAO code from the provider.
     *
     * @param icaoCode the ICAO airport code to lookup
     * @return Optional containing airport information if found, empty otherwise
     */
    Optional<AirportInfo> getAirportByIcaoCode(String icaoCode);
}