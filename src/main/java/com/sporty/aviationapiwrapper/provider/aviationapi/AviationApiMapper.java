package com.sporty.aviationapiwrapper.provider.aviationapi;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.dto.AviationApiAirportData;
import org.springframework.stereotype.Component;

@Component
public class AviationApiMapper {

    private static final String SOURCE = "aviationapi.com";

    public AirportInfo toAirportInfo(String icaoCode, AviationApiAirportData data) {
        return AirportInfo.builder()
                .icao(icaoCode.toUpperCase())
                .iata(null)
                .name(data.getFacilityName())
                .city(data.getCity())
                .region(data.getStateFull())
                .country("US")
                .latitude(data.getLatitude())
                .longitude(data.getLongitude())
                .elevationFt(data.getElevation())
                .timezone(null)
                .source(SOURCE)
                .build();
    }
}