package com.sporty.aviationapiwrapper.controller;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.service.AirportService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/airports")
@RequiredArgsConstructor
@Validated
public class AirportController {

    private final AirportService airportService;

    @GetMapping("/{icao}")
    public ResponseEntity<AirportInfo> getAirportByIcao(
            @PathVariable("icao")
            @Pattern(regexp = "^[A-Z]{4}$", message = "ICAO code must be exactly 4 uppercase letters")
            String icaoCode) {
        return airportService.getAirportByIcao(icaoCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}