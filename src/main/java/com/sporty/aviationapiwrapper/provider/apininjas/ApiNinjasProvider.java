package com.sporty.aviationapiwrapper.provider.apininjas;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.AirportDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("apiNinjasProvider")
@RequiredArgsConstructor
public class ApiNinjasProvider implements AirportDataProvider {

    private final ApiNinjasClient client;
    private final ApiNinjasMapper mapper;

    @Override
    public Optional<AirportInfo> getAirportByIcaoCode(String icaoCode) {
        return client.fetchAirport(icaoCode)
                .map(mapper::toAirportInfo);
    }
}