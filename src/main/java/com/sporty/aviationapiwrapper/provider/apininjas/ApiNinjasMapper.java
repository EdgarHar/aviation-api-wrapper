package com.sporty.aviationapiwrapper.provider.apininjas;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.dto.ApiNinjasAirport;
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
                .latitude(airport.getLatitude())
                .longitude(airport.getLongitude())
                .elevationFt(airport.getElevationFt())
                .timezone(airport.getTimezone())
                .source(SOURCE)
                .build();
    }
}