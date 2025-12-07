package com.sporty.aviationapiwrapper.controller;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.dto.ErrorResponse;
import com.sporty.aviationapiwrapper.exception.AirportNotFoundException;
import com.sporty.aviationapiwrapper.service.AirportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Airports", description = "Airport information retrieval API with multi-provider fallback support")
public class AirportController {

    private final AirportService airportService;

    @Operation(
            summary = "Get airport information by ICAO code",
            description = "Retrieves airport details using the ICAO code. The service aggregates data from multiple aviation providers with automatic fallback, caching, and circuit breaker patterns for high availability."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Airport information successfully retrieved",
                    content = @Content(schema = @Schema(implementation = AirportInfo.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ICAO code format (must be exactly 4 uppercase letters)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Airport not found for the provided ICAO code",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error or all providers unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{icao}")
    public ResponseEntity<AirportInfo> getAirportByIcao(
            @Parameter(
                    description = "ICAO airport code (4 uppercase letters)",
                    example = "KJFK",
                    required = true
            )
            @PathVariable("icao")
            @Pattern(regexp = "^[A-Z]{4}$", message = "ICAO code must be exactly 4 uppercase letters")
            String icaoCode) {
        return ResponseEntity.ok(airportService.getAirportByIcao(icaoCode)
                .orElseThrow(() -> new AirportNotFoundException(
                        "Airport not found for ICAO code: " + icaoCode)));
    }
}