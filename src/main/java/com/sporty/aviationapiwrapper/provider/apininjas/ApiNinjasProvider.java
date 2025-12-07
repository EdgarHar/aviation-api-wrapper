package com.sporty.aviationapiwrapper.provider.apininjas;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.AirportDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provider implementation for <a href="https://api-ninjas.com">...</a>.
 * Fetches airport data from the API Ninjas service and maps it to the common domain model.
 */
@Component("apiNinjasProvider")
@RequiredArgsConstructor
public class ApiNinjasProvider implements AirportDataProvider {

    private final ApiNinjasClient client;
    private final ApiNinjasMapper mapper;

    /**
     * Retrieves airport information from <a href="https://api-ninjas.com">...</a> by ICAO code.
     * Fetches data via the client and transforms it to the common AirportInfo format.
     *
     * @param icaoCode the ICAO airport code to lookup
     * @return Optional containing mapped airport information if found, empty otherwise
     */
    @Override
    public Optional<AirportInfo> getAirportByIcaoCode(String icaoCode) {
        return client.fetchAirport(icaoCode)
                .map(mapper::toAirportInfo);
    }
}