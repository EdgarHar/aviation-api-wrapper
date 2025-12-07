package com.sporty.aviationapiwrapper.provider.apininjas;

import com.sporty.aviationapiwrapper.utils.FeignClientExecuteUtils;
import com.sporty.aviationapiwrapper.dto.ApiNinjasAirport;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class ApiNinjasClient {

    private static final String PROVIDER_NAME = "apiNinjas";

    private final ApiNinjasFeignClient feignClient;
    private final String apiKey;

    public ApiNinjasClient(ApiNinjasFeignClient feignClient,
                           @Value("${api.providers.apiNinjas.apiKey:}") String apiKey) {
        this.feignClient = feignClient;
        this.apiKey = apiKey;
    }

    /**
     * Fetches airport data from the API Ninjas provider (<a href="https://api-ninjas.com">...</a>).
     * Applies circuit breaker and retry patterns for resilience.
     * On server errors (5xx), retries are attempted based on configuration.
     * On client errors (4xx), no retries are performed.
     *
     * @param icaoCode the ICAO airport code to fetch
     * @return Optional containing the first airport match if found, empty on errors or not found
     */
    @CircuitBreaker(name = "apiNinjas")
    @Retry(name = "apiNinjas", fallbackMethod = "fetchAirportFallback")
    public Optional<ApiNinjasAirport> fetchAirport(String icaoCode) {
        return FeignClientExecuteUtils.execute(PROVIDER_NAME, icaoCode,
                        () -> feignClient.getAirport(icaoCode.toUpperCase(), apiKey))
                .map(Arrays::asList)
                .flatMap(list -> list.stream().findFirst());
    }

    /**
     * Fallback method invoked when all retry attempts are exhausted.
     *
     * @param icaoCode the ICAO airport code that was being fetched
     * @param ex the exception that triggered the fallback
     * @return empty Optional
     */
    public Optional<ApiNinjasAirport> fetchAirportFallback(String icaoCode, Throwable ex) {
        log.warn("[{}] Fallback after retries for ICAO: {} | {}: {}",
                PROVIDER_NAME, icaoCode, ex.getClass().getSimpleName(), ex.getMessage());
        return Optional.empty();
    }
}