package com.sporty.aviationapiwrapper.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sporty.aviationapiwrapper.AbstractIntegrationTest;
import com.sporty.aviationapiwrapper.dto.ApiNinjasAirport;
import com.sporty.aviationapiwrapper.dto.AviationApiAirportData;
import com.sporty.aviationapiwrapper.dto.AviationApiAirportResponse;
import com.sporty.aviationapiwrapper.provider.apininjas.ApiNinjasFeignClient;
import com.sporty.aviationapiwrapper.provider.aviationapi.AviationApiFeignClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class AirportApiIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private CircuitBreakerRegistry circuitBreakerRegistry;

  @Autowired private CacheManager cacheManager;

  @MockBean private AviationApiFeignClient aviationApiFeignClient;

  @MockBean private ApiNinjasFeignClient apiNinjasFeignClient;

  @BeforeEach
  void setUp() {
    circuitBreakerRegistry
        .getAllCircuitBreakers()
        .forEach(
            circuitBreaker -> {
              circuitBreaker.reset();
              circuitBreaker.transitionToClosedState();
            });

    cacheManager
        .getCacheNames()
        .forEach(
            cacheName -> {
              var cache = cacheManager.getCache(cacheName);
              if (cache != null) {
                cache.clear();
              }
            });

    reset(aviationApiFeignClient, apiNinjasFeignClient);
  }

  @Test
  void shouldReturnAirportFromPrimaryProvider() throws Exception {
    AviationApiAirportData apiAirportData = new AviationApiAirportData();
    apiAirportData.setFacilityName("John F Kennedy International Airport");
    apiAirportData.setCity("New York");
    AviationApiAirportResponse airportResponse = new AviationApiAirportResponse();
    airportResponse.put("KJFK", List.of(apiAirportData));

    when(aviationApiFeignClient.getAirport("KJFK")).thenReturn(airportResponse);

    mockMvc
        .perform(get("/api/v1/airports/KJFK"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.icao").value("KJFK"))
        .andExpect(jsonPath("$.name").value("John F Kennedy International Airport"))
        .andExpect(jsonPath("$.city").value("New York"));

    verify(aviationApiFeignClient, times(1)).getAirport("KJFK");
    verifyNoInteractions(apiNinjasFeignClient);
  }

  @Test
  void shouldReturnFromCacheOnSecondRequest() throws Exception {
    AviationApiAirportData apiAirportData = new AviationApiAirportData();
    apiAirportData.setFacilityName("London Heathrow");
    apiAirportData.setCity("London");
    AviationApiAirportResponse airportResponse = new AviationApiAirportResponse();
    airportResponse.put("EGLL", List.of(apiAirportData));

    when(aviationApiFeignClient.getAirport("EGLL")).thenReturn(airportResponse);

    mockMvc
        .perform(get("/api/v1/airports/EGLL"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.icao").value("EGLL"))
        .andExpect(jsonPath("$.city").value("London"));

    mockMvc
        .perform(get("/api/v1/airports/EGLL"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.icao").value("EGLL"))
        .andExpect(jsonPath("$.city").value("London"));

    verify(aviationApiFeignClient, times(1)).getAirport("EGLL");
  }

  @Test
  void shouldReturn404WhenAllProvidersReturnEmpty() throws Exception {
    when(aviationApiFeignClient.getAirport("ZZZZ")).thenReturn(null);
    when(apiNinjasFeignClient.getAirport(eq("ZZZZ"), anyString()))
        .thenReturn(new ApiNinjasAirport[0]);

    mockMvc
        .perform(get("/api/v1/airports/ZZZZ"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Airport not found for ICAO code: ZZZZ"))
        .andExpect(jsonPath("$.status").value(404));

    verify(aviationApiFeignClient, times(1)).getAirport("ZZZZ");
    verify(apiNinjasFeignClient, times(1)).getAirport(eq("ZZZZ"), anyString());
  }

  @Test
  void shouldRetryOnServerErrorAndEventuallySucceed() throws Exception {
    AviationApiAirportData apiAirportData = new AviationApiAirportData();
    apiAirportData.setFacilityName("Charles de Gaulle");
    apiAirportData.setCity("Paris");
    AviationApiAirportResponse aviationApiAirportResponse = new AviationApiAirportResponse();
    aviationApiAirportResponse.put("LFPG", List.of(apiAirportData));

    when(aviationApiFeignClient.getAirport("LFPG"))
        .thenThrow(createFeignException(503))
        .thenReturn(aviationApiAirportResponse);

    mockMvc
        .perform(get("/api/v1/airports/LFPG"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.icao").value("LFPG"))
        .andExpect(jsonPath("$.name").value("Charles de Gaulle"));

    verify(aviationApiFeignClient, times(2)).getAirport("LFPG");
  }

  @Test
  void shouldNotRetryOn4xxClientError() throws Exception {
    when(aviationApiFeignClient.getAirport("EDDF")).thenThrow(createFeignException(404));

    ApiNinjasAirport fallbackResponse = new ApiNinjasAirport();
    fallbackResponse.setIcao("EDDF");
    fallbackResponse.setName("Frankfurt Airport");
    fallbackResponse.setCity("Frankfurt");
    fallbackResponse.setCountry("DE");

    when(apiNinjasFeignClient.getAirport(eq("EDDF"), anyString()))
        .thenReturn(new ApiNinjasAirport[] {fallbackResponse});

    mockMvc
        .perform(get("/api/v1/airports/EDDF"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.icao").value("EDDF"));

    verify(aviationApiFeignClient, times(1)).getAirport("EDDF");
    verify(apiNinjasFeignClient, times(1)).getAirport(eq("EDDF"), anyString());
  }

  @Test
  void shouldReturnBadRequestForInvalidIcaoFormat() throws Exception {
    mockMvc
        .perform(get("/api/v1/airports/ABC"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("ICAO code must be exactly 4 uppercase letters"));

    verifyNoInteractions(aviationApiFeignClient, apiNinjasFeignClient);
  }

  private FeignException createFeignException(int status) {
    Request request =
        Request.create(
            Request.HttpMethod.GET, "/test", new HashMap<>(), null, new RequestTemplate());
    return FeignException.errorStatus(
        "test",
        feign.Response.builder()
            .status(status)
            .reason("Test")
            .request(request)
            .headers(new HashMap<>())
            .build());
  }
}
