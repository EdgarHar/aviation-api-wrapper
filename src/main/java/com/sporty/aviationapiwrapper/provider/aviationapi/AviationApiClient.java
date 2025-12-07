package com.sporty.aviationapiwrapper.provider.aviationapi;

import com.sporty.aviationapiwrapper.utils.FeignClientExecuteUtils;
import com.sporty.aviationapiwrapper.dto.AviationApiAirportResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AviationApiClient {

    private static final String PROVIDER_NAME = "aviationApi";

    private final AviationApiFeignClient feignClient;

    /**
     * Fetches airport data from the Aviation API provider (<a href="https://api.aviationapi.com">...</a>).
     * Applies circuit breaker and retry patterns for resilience.
     * On server errors (5xx), retries are attempted based on configuration.
     * On client errors (4xx), no retries are performed.
     *
     * @param icaoCode the ICAO airport code to fetch
     * @return Optional containing airport data if found, empty on errors or not found
     */
    @CircuitBreaker(name = "aviationApi")
    @Retry(name = "aviationApi", fallbackMethod = "fetchAirportFallback")
    public Optional<AviationApiAirportResponse> fetchAirport(String icaoCode) {
        return FeignClientExecuteUtils.execute(PROVIDER_NAME, icaoCode,
                () -> feignClient.getAirport(icaoCode.toUpperCase()));
    }

    /**
     * Fallback method invoked when all retry attempts are exhausted.
     *
     * @param icaoCode the ICAO airport code that was being fetched
     * @param ex the exception that triggered the fallback
     * @return empty Optional
     */
    public Optional<AviationApiAirportResponse> fetchAirportFallback(String icaoCode, Throwable ex) {
        log.warn("[{}] Fallback after retries for ICAO: {} | {}: {}",
                PROVIDER_NAME, icaoCode, ex.getClass().getSimpleName(), ex.getMessage());
        return Optional.empty();
    }
}