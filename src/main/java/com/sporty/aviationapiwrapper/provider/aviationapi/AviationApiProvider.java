package com.sporty.aviationapiwrapper.provider.aviationapi;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.AirportDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("aviationApiProvider")
@RequiredArgsConstructor
public class AviationApiProvider implements AirportDataProvider {

    private final AviationApiClient client;
    private final AviationApiMapper mapper;

    @Override
    public Optional<AirportInfo> getAirportByIcaoCode(String icaoCode) {
        return client.fetchAirport(icaoCode)
                .flatMap(response -> response.getAirportData(icaoCode).stream().findFirst())
                .map(data -> mapper.toAirportInfo(icaoCode, data));
    }
}
