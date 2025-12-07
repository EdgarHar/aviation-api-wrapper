package com.sporty.aviationapiwrapper.service;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import com.sporty.aviationapiwrapper.provider.AirportDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AirportDataProvider airportDataProvider;
    @Mock
    private AirportCacheService cacheService;

    @InjectMocks
    private AirportService airportService;

    @Test
    void testGetAirportByIcao_hitsCache() {
        String icao = "UDYZ";
        AirportInfo cachedInfo = new AirportInfo();
        when(cacheService.get(icao)).thenReturn(Optional.of(cachedInfo));

        Optional<AirportInfo> result = airportService.getAirportByIcao(icao);

        assertTrue(result.isPresent());
        assertEquals(cachedInfo, result.get());

        verify(cacheService).get(icao);
        verify(cacheService, never()).put(any(), any());
        verifyNoInteractions(airportDataProvider);
    }

    @Test
    void testGetAirportByIcao_missesCacheAndFetches() {
        String icao = "UDYZ";
        AirportInfo fetchedInfo = new AirportInfo();

        when(cacheService.get(icao)).thenReturn(Optional.empty());
        when(airportDataProvider.getAirportByIcaoCode(icao))
                .thenReturn(Optional.of(fetchedInfo));

        Optional<AirportInfo> result = airportService.getAirportByIcao(icao);

        assertTrue(result.isPresent());
        assertEquals(fetchedInfo, result.get());

        verify(cacheService).get(icao);
        verify(airportDataProvider).getAirportByIcaoCode(icao);
        verify(cacheService).put(icao, fetchedInfo);
    }
}
