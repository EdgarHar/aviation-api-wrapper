package com.sporty.aviationapiwrapper.provider.aviationapi;

import com.sporty.aviationapiwrapper.config.properties.ApiProperties;
import com.sporty.aviationapiwrapper.config.properties.ProviderProperties;
import com.sporty.aviationapiwrapper.dto.GetApiRequest;
import com.sporty.aviationapiwrapper.provider.aviationapi.dto.AirportResponse;
import com.sporty.aviationapiwrapper.service.RestService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AviationApiClient {

    private static final String PROVIDER_NAME = "aviationApi";

    private final RestService restService;
    private final ProviderProperties properties;

    public AviationApiClient(RestService restService, ApiProperties apiProperties) {
        this.restService = restService;
        this.properties = apiProperties.getProvider(PROVIDER_NAME);
    }

    @CircuitBreaker(name = "aviationApi")
    @Retry(name = "aviationApi", fallbackMethod = "fetchAirportFallback")
    public Optional<AirportResponse> fetchAirport(String icaoCode) {
        return Optional.ofNullable(properties.getEndpoints().get("airports"))
                .map(endpoint -> properties.getBaseUrl() + endpoint)
                .map(url -> GetApiRequest.<AirportResponse>builder()
                        .url(url)
                        .queryParams(Map.of("apt", icaoCode.toUpperCase()))
                        .responseType(AirportResponse.class)
                        .build())
                .flatMap(restService::get);
    }

    public Optional<AirportResponse> fetchAirportFallback(String icaoCode, Exception ex) {
        log.info("AviationApi fallback triggered for ICAO: {} | Exception: {} - {}",
                icaoCode, ex.getClass().getSimpleName(), ex.getMessage());
        return Optional.empty();
    }
}