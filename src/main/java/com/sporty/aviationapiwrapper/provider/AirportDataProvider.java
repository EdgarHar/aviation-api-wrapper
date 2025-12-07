package com.sporty.aviationapiwrapper.provider;

import com.sporty.aviationapiwrapper.dto.AirportInfo;

import java.util.Optional;

public interface AirportDataProvider {
    Optional<AirportInfo> getAirportByIcaoCode(String icaoCode);
}