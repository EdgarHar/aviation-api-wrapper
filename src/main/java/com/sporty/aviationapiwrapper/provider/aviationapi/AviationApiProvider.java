package com.sporty.aviationapiwrapper.provider.aviationapi;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.AirportDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provider implementation for <a href="https://api.aviationapi.com">...</a>.
 * Fetches airport data from the Aviation API and maps it to the common domain model.
 */
@Component("aviationApiProvider")
@RequiredArgsConstructor
public class AviationApiProvider implements AirportDataProvider {

    private final AviationApiClient client;
    private final AviationApiMapper mapper;

    /**
     * Retrieves airport information from <a href="https://api.aviationapi.com">...</a> by ICAO code.
     * Fetches data via the client and transforms it to the common AirportInfo format.
     *
     * @param icaoCode the ICAO airport code to lookup
     * @return Optional containing mapped airport information if found, empty otherwise
     */
    @Override
    public Optional<AirportInfo> getAirportByIcaoCode(String icaoCode) {
        return client.fetchAirport(icaoCode)
                .flatMap(response -> response.getAirportData(icaoCode).stream().findFirst())
                .map(data -> mapper.toAirportInfo(icaoCode, data));
    }
}
