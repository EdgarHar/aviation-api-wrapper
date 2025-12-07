package com.sporty.aviationapiwrapper.apininjas;

import com.sporty.aviationapiwrapper.provider.apininjas.ApiNinjasClient;
import com.sporty.aviationapiwrapper.provider.apininjas.ApiNinjasFeignClient;
import com.sporty.aviationapiwrapper.dto.ApiNinjasAirport;
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

    private static final String API_KEY = "dummy-key";

    @Mock
    private ApiNinjasFeignClient feignClient;

    private ApiNinjasClient client;

    @BeforeEach
    void setup() {
        client = new ApiNinjasClient(feignClient, API_KEY);
    }

    @Test
    void testFetchAirport_success() {
        String icao = "UDYZ";
        ApiNinjasAirport airport = new ApiNinjasAirport();

        when(feignClient.getAirport("UDYZ", API_KEY)).thenReturn(new ApiNinjasAirport[]{airport});

        Optional<ApiNinjasAirport> result = client.fetchAirport(icao);

        assertTrue(result.isPresent());
        assertEquals(airport, result.get());
        verify(feignClient).getAirport("UDYZ", API_KEY);
    }

    @Test
    void testFetchAirport_noEndpoint() {
        when(feignClient.getAirport("UDYZ", API_KEY)).thenReturn(null);

        Optional<ApiNinjasAirport> result = client.fetchAirport("UDYZ");

        assertTrue(result.isEmpty());
        verify(feignClient).getAirport("UDYZ", API_KEY);
    }
}
