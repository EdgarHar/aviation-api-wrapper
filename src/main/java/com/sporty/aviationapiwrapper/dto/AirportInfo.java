package com.sporty.aviationapiwrapper.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirportInfo {
    private String icao;
    private String iata;
    private String name;
    private String city;
    private String region;
    private String country;
    private Double latitude;
    private Double longitude;
    private Integer elevationFt;
    private String timezone;
    private String source;
}