package com.sporty.aviationapiwrapper.aviationapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sporty.aviationapiwrapper.provider.aviationapi.AviationApiClient;
import com.sporty.aviationapiwrapper.provider.aviationapi.AviationApiFeignClient;
import com.sporty.aviationapiwrapper.provider.aviationapi.dto.AirportResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AviationApiClientTest {

    @Mock
    private AviationApiFeignClient feignClient;

    private AviationApiClient client;

    @BeforeEach
    void setup() {
        client = new AviationApiClient(feignClient);
    }

    @Test
    void testFetchAirport_success() {
        String icao = "UDYZ";
        AirportResponse response = new AirportResponse();

        when(feignClient.getAirport("UDYZ")).thenReturn(response);

        Optional<AirportResponse> result = client.fetchAirport(icao);

        assertTrue(result.isPresent());
        assertEquals(response, result.get());
        verify(feignClient).getAirport("UDYZ");
    }

    @Test
    void testFetchAirport_noEndpoint() {
        when(feignClient.getAirport("UDYZ")).thenReturn(null);

        Optional<AirportResponse> result = client.fetchAirport("UDYZ");

        assertTrue(result.isEmpty());
        verify(feignClient).getAirport("UDYZ");
    }
}
