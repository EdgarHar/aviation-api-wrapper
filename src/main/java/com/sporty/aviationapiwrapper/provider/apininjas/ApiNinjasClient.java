package com.sporty.aviationapiwrapper.provider.apininjas;

import com.sporty.aviationapiwrapper.config.properties.ApiProperties;
import com.sporty.aviationapiwrapper.provider.apininjas.dto.ApiNinjasAirport;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class ApiNinjasClient {

    private static final String PROVIDER_NAME = "apiNinjas";

    private final ApiNinjasFeignClient feignClient;
    private final String apiKey;

    public ApiNinjasClient(ApiNinjasFeignClient feignClient, ApiProperties apiProperties) {
        this.feignClient = feignClient;
        this.apiKey = apiProperties.getProvider(PROVIDER_NAME).getApiKey();
    }

    @CircuitBreaker(name = "apiNinjas")
    @Retry(name = "apiNinjas", fallbackMethod = "fetchAirportFallback")
    public Optional<ApiNinjasAirport> fetchAirport(String icaoCode) {
        return Optional.ofNullable(feignClient.getAirport(icaoCode.toUpperCase(), apiKey))
                .map(Arrays::asList)
                .flatMap(list -> list.stream().findFirst());
    }

    public Optional<ApiNinjasAirport> fetchAirportFallback(String icaoCode, Throwable ex) {
        log.warn("ApiNinjas fallback triggered for ICAO: {} | Exception: {} - {}",
                icaoCode, ex.getClass().getSimpleName(), ex.getMessage());
        return Optional.empty();
    }
}