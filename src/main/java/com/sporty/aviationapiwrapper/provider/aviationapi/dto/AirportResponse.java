package com.sporty.aviationapiwrapper.provider.aviationapi.dto;

import com.sporty.aviationapiwrapper.dto.AirportData;

import java.util.*;

public class AirportResponse extends HashMap<String, List<AirportData>> {

  public List<AirportData> getAirportData(String icaoCode) {
    return Optional.of(icaoCode).map(this::get).orElseGet(ArrayList::new);
  }

}
