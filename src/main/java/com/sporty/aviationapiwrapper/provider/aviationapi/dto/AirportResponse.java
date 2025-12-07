package com.sporty.aviationapiwrapper.provider.aviationapi.dto;

import com.sporty.aviationapiwrapper.dto.AviationApiAirportData;

import java.util.*;

public class AirportResponse extends HashMap<String, List<AviationApiAirportData>> {

  public List<AviationApiAirportData> getAirportData(String icaoCode) {
    return Optional.of(icaoCode).map(this::get).orElseGet(ArrayList::new);
  }

}
