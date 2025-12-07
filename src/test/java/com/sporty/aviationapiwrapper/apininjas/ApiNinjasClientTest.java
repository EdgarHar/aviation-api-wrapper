package com.sporty.aviationapiwrapper.apininjas;

import com.sporty.aviationapiwrapper.config.properties.ApiProperties;
import com.sporty.aviationapiwrapper.config.properties.ProviderProperties;
import com.sporty.aviationapiwrapper.provider.apininjas.ApiNinjasClient;
import com.sporty.aviationapiwrapper.provider.apininjas.ApiNinjasFeignClient;
import com.sporty.aviationapiwrapper.provider.apininjas.dto.ApiNinjasAirport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiNinjasClientTest {

    @Mock
    private ApiNinjasFeignClient feignClient;
    @Mock
    private ApiProperties apiProperties;
    @Mock
    private ProviderProperties providerProperties;

    private ApiNinjasClient client;

    @BeforeEach
    void setup() {
        when(apiProperties.getProvider("apiNinjas")).thenReturn(providerProperties);
        when(providerProperties.getApiKey()).thenReturn("dummy-key");
        client = new ApiNinjasClient(feignClient, apiProperties);
    }

    @Test
    void testFetchAirport_success() {
        String icao = "UDYZ";
        ApiNinjasAirport airport = new ApiNinjasAirport();

        when(feignClient.getAirport("UDYZ", "dummy-key")).thenReturn(new ApiNinjasAirport[]{airport});

        Optional<ApiNinjasAirport> result = client.fetchAirport(icao);

        assertTrue(result.isPresent());
        assertEquals(airport, result.get());
        verify(feignClient).getAirport("UDYZ", "dummy-key");
    }

    @Test
    void testFetchAirport_noEndpoint() {
        when(feignClient.getAirport("UDYZ", "dummy-key")).thenReturn(null);

        Optional<ApiNinjasAirport> result = client.fetchAirport("UDYZ");

        assertTrue(result.isEmpty());
        verify(feignClient).getAirport("UDYZ", "dummy-key");
    }
}
