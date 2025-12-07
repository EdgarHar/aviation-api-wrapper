package com.sporty.aviationapiwrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiNinjasAirport {
    private String icao;
    private String iata;
    private String name;
    private String city;
    private String region;
    private String country;
    @JsonProperty("elevation_ft")
    private String elevationFt;
    private String latitude;
    private String longitude;
    private String timezone;
}