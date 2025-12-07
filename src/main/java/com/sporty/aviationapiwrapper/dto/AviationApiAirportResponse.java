package com.sporty.aviationapiwrapper.dto;

import java.util.*;

public class AviationApiAirportResponse extends HashMap<String, List<AviationApiAirportData>> {

  public List<AviationApiAirportData> getAirportData(String icaoCode) {
    return Optional.of(icaoCode).map(this::get).orElseGet(ArrayList::new);
  }

}
