package com.sporty.aviationapiwrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationApiAirportData {
    private String city;
    @JsonProperty("state_full")
    private String stateFull;
    private String county;
    private String elevation;
    private String latitude;
    private String longitude;
    private String use;
    @JsonProperty("control_tower")
    private String controlTower;
    private String ctaf;
    private String unicom;
    private String status;
    @JsonProperty("notam_facility_ident")
    private String notamFacilityIdent;
    @JsonProperty("responsible_artcc")
    private String responsibleArtcc;
    @JsonProperty("responsible_artcc_name")
    private String responsibleArtccName;
}