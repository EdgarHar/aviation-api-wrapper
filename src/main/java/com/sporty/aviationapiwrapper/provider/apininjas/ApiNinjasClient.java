package com.sporty.aviationapiwrapper.provider.apininjas;

import com.sporty.aviationapiwrapper.config.properties.ApiProperties;
import com.sporty.aviationapiwrapper.config.properties.ProviderProperties;
import com.sporty.aviationapiwrapper.dto.GetApiRequest;
import com.sporty.aviationapiwrapper.provider.apininjas.dto.ApiNinjasAirport;
import com.sporty.aviationapiwrapper.service.RestService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class ApiNinjasClient {

    private static final String PROVIDER_NAME = "apiNinjas";
    private static final String API_KEY_HEADER = "X-Api-Key";

    private final RestService restService;
    private final ProviderProperties properties;

    public ApiNinjasClient(RestService restService, ApiProperties apiProperties) {
        this.restService = restService;
        this.properties = apiProperties.getProvider(PROVIDER_NAME);
    }

    @CircuitBreaker(name = "apiNinjas")
    @Retry(name = "apiNinjas", fallbackMethod = "fetchAirportFallback")
    public Optional<ApiNinjasAirport> fetchAirport(String icaoCode) {
        return Optional.ofNullable(properties.getEndpoints().get("airports"))
                .map(endpoint -> properties.getBaseUrl() + endpoint)
                .map(url -> GetApiRequest.<ApiNinjasAirport[]>builder()
                        .url(url)
                        .queryParams(Map.of("icao", icaoCode.toUpperCase()))
                        .headers(Map.of(API_KEY_HEADER, properties.getApiKey()))
                        .responseType(ApiNinjasAirport[].class)
                        .build())
                .flatMap(restService::get)
                .map(Arrays::asList)
                .flatMap(list -> list.stream().findFirst());
    }

    public Optional<ApiNinjasAirport> fetchAirportFallback(String icaoCode, Exception ex) {
        log.info("ApiNinjas fallback triggered for ICAO: {} | Exception: {} - {}",
                icaoCode, ex.getClass().getSimpleName(), ex.getMessage());
        return Optional.empty();
    }
}