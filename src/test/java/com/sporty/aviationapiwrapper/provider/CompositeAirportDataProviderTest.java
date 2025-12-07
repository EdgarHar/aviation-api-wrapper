package com.sporty.aviationapiwrapper.provider;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.aviationapi.AviationApiProvider;
import com.sporty.aviationapiwrapper.provider.apininjas.ApiNinjasProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompositeAirportDataProviderTest {

    private final String ICAO = "UDYZ";

    @Mock
    private AviationApiProvider aviationApiProvider;
    @Mock
    private ApiNinjasProvider apiNinjasProvider;

    private CompositeAirportDataProvider compositeProvider;

    private final AirportInfo airportInfo = new AirportInfo();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        compositeProvider = new CompositeAirportDataProvider(
                List.of(aviationApiProvider, apiNinjasProvider)
        );
    }

    @Test
    void testFirstProviderSucceeds() {
        when(aviationApiProvider.getAirportByIcaoCode(ICAO)).thenReturn(Optional.of(airportInfo));

        Optional<AirportInfo> result = compositeProvider.getAirportByIcaoCode(ICAO);

        assertTrue(result.isPresent());
        assertEquals(airportInfo, result.get());
        verify(aviationApiProvider).getAirportByIcaoCode(ICAO);
        verifyNoInteractions(apiNinjasProvider);
    }

    @Test
    void testFirstFailsSecondSucceeds() {
        when(aviationApiProvider.getAirportByIcaoCode(ICAO)).thenReturn(Optional.empty());
        when(apiNinjasProvider.getAirportByIcaoCode(ICAO)).thenReturn(Optional.of(airportInfo));

        Optional<AirportInfo> result = compositeProvider.getAirportByIcaoCode(ICAO);

        assertTrue(result.isPresent());
        assertEquals(airportInfo, result.get());
        verify(aviationApiProvider).getAirportByIcaoCode(ICAO);
        verify(apiNinjasProvider).getAirportByIcaoCode(ICAO);
    }

    @Test
    void testAllProvidersFail() {
        when(aviationApiProvider.getAirportByIcaoCode(ICAO)).thenReturn(Optional.empty());
        when(apiNinjasProvider.getAirportByIcaoCode(ICAO)).thenReturn(Optional.empty());

        Optional<AirportInfo> result = compositeProvider.getAirportByIcaoCode(ICAO);

        assertTrue(result.isEmpty());
        verify(aviationApiProvider).getAirportByIcaoCode(ICAO);
        verify(apiNinjasProvider).getAirportByIcaoCode(ICAO);
    }
}
