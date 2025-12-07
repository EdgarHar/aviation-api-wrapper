package com.sporty.aviationapiwrapper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportInfo implements Serializable {
    private String icao;
    private String iata;
    private String name;
    private String city;
    private String region;
    private String country;
    private String latitude;
    private String longitude;
    private String elevationFt;
    private String timezone;
    private String source;
}