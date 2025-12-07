package com.sporty.aviationapiwrapper.service;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportCacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private MeterRegistry meterRegistry;
    private AirportCacheService cacheService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        cacheService = new AirportCacheService(cacheManager, meterRegistry);
    }

    @Test
    void shouldReturnAirportInfoOnCacheHitAndIncrementCounter() {
        AirportInfo expectedInfo = createAirportInfo("KJFK", "JFK Airport");
        when(cacheManager.getCache("airports")).thenReturn(cache);
        when(cache.get("KJFK", AirportInfo.class)).thenReturn(expectedInfo);

        Optional<AirportInfo> result = cacheService.get("KJFK");

        assertTrue(result.isPresent());
        assertEquals(expectedInfo, result.get());
        assertEquals(1.0, meterRegistry.find("airport.cache.hits").counter().count());
    }

    @Test
    void shouldReturnEmptyOnCacheMissAndIncrementCounter() {
        when(cacheManager.getCache("airports")).thenReturn(cache);
        when(cache.get("ZZZZ", AirportInfo.class)).thenReturn(null);

        Optional<AirportInfo> result = cacheService.get("ZZZZ");

        assertFalse(result.isPresent());
        assertEquals(1.0, meterRegistry.find("airport.cache.misses").counter().count());
    }

    @Test
    void shouldNormalizeKeyToUppercase() {
        when(cacheManager.getCache("airports")).thenReturn(cache);

        cacheService.get("kjfk");
        cacheService.put("eddf", createAirportInfo("EDDF", "Frankfurt"));

        verify(cache).get("KJFK", AirportInfo.class);
        verify(cache).put(eq("EDDF"), any(AirportInfo.class));
    }

    @Test
    void shouldHandleNullCacheGracefully() {
        when(cacheManager.getCache("airports")).thenReturn(null);

        Optional<AirportInfo> result = cacheService.get("KJFK");
        assertFalse(result.isPresent());

        assertDoesNotThrow(() -> cacheService.put("KJFK", createAirportInfo("KJFK", "JFK")));
    }

    private AirportInfo createAirportInfo(String icao, String name) {
        return AirportInfo.builder()
                .icao(icao)
                .name(name)
                .city("Test City")
                .country("Test Country")
                .build();
    }
}