package com.sporty.aviationapiwrapper.provider.apininjas;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.apininjas.dto.ApiNinjasAirport;
import com.sporty.aviationapiwrapper.util.NumberParseUtils;
import org.springframework.stereotype.Component;

@Component
public class ApiNinjasMapper {

    private static final String SOURCE = "api-ninjas.com";

    public AirportInfo toAirportInfo(ApiNinjasAirport airport) {
        return AirportInfo.builder()
                .icao(airport.getIcao())
                .iata(airport.getIata())
                .name(airport.getName())
                .city(airport.getCity())
                .region(airport.getRegion())
                .country(airport.getCountry())
                .latitude(NumberParseUtils.parseDouble(airport.getLatitude()))
                .longitude(NumberParseUtils.parseDouble(airport.getLongitude()))
                .elevationFt(NumberParseUtils.parseInteger(airport.getElevationFt()))
                .timezone(airport.getTimezone())
                .source(SOURCE)
                .build();
    }
}