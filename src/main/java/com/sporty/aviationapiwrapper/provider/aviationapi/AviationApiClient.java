package com.sporty.aviationapiwrapper.provider.aviationapi;

import com.sporty.aviationapiwrapper.provider.aviationapi.dto.AirportResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AviationApiClient {

    private final AviationApiFeignClient feignClient;

    @CircuitBreaker(name = "aviationApi")
    @Retry(name = "aviationApi", fallbackMethod = "fetchAirportFallback")
    public Optional<AirportResponse> fetchAirport(String icaoCode) {
        return Optional.ofNullable(feignClient.getAirport(icaoCode.toUpperCase()));
    }

    public Optional<AirportResponse> fetchAirportFallback(String icaoCode, Throwable ex) {
        log.warn("AviationApi fallback triggered for ICAO: {} | Exception: {} - {}",
                icaoCode, ex.getClass().getSimpleName(), ex.getMessage());
        return Optional.empty();
    }
}