package com.sporty.aviationapiwrapper.service;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.AirportDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirportService {

    private final AirportDataProvider airportDataProvider;

    public AirportService(@Qualifier("aviationApiProvider") AirportDataProvider airportDataProvider) {
        this.airportDataProvider = airportDataProvider;
    };

    public Optional<AirportInfo> getAirportByIcao(String icaoCode) {
        return airportDataProvider.getAirportByIcaoCode(icaoCode);
    }
}
