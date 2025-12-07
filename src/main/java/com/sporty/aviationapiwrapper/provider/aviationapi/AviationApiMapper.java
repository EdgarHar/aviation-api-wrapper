package com.sporty.aviationapiwrapper.provider.aviationapi;

import com.sporty.aviationapiwrapper.dto.AviationApiAirportData;
import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.util.NumberParseUtils;
import org.springframework.stereotype.Component;

@Component
public class AviationApiMapper {

    private static final String SOURCE = "aviationapi.com";

    public AirportInfo toAirportInfo(String icaoCode, AviationApiAirportData data) {
        return AirportInfo.builder()
                .icao(icaoCode.toUpperCase())
                .iata(null)
                .name(null)
                .city(data.getCity())
                .region(data.getStateFull())
                .country("US")
                .latitude(NumberParseUtils.parseDouble(data.getLatitude()))
                .longitude(NumberParseUtils.parseDouble(data.getLongitude()))
                .elevationFt(NumberParseUtils.parseInteger(data.getElevation()))
                .timezone(null)
                .source(SOURCE)
                .build();
    }
}