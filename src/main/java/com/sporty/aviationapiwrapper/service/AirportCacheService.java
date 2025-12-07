package com.sporty.aviationapiwrapper.service;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AirportCacheService {

    private static final String CACHE_NAME = "airports";

    private final CacheManager cacheManager;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    public AirportCacheService(CacheManager cacheManager, MeterRegistry meterRegistry) {
        this.cacheManager = cacheManager;
        this.cacheHitCounter = Counter.builder("airport.cache.hits")
                .description("Number of cache hits for airport lookups")
                .tag("cache", CACHE_NAME)
                .register(meterRegistry);
        this.cacheMissCounter = Counter.builder("airport.cache.misses")
                .description("Number of cache misses for airport lookups")
                .tag("cache", CACHE_NAME)
                .register(meterRegistry);
    }

    /**
     * Retrieves airport information from the cache.
     * Increments cache hit or miss metrics accordingly.
     *
     * @param icaoCode the ICAO airport code to lookup
     * @return Optional containing cached airport information if found, empty otherwise
     */
    public Optional<AirportInfo> get(String icaoCode) {
        return executeCacheOperation("get", () ->
                getCache()
                        .flatMap(cache -> Optional.ofNullable(cache.get(normalizeKey(icaoCode), AirportInfo.class)))
                        .map(info -> {
                            cacheHitCounter.increment();
                            log.debug("Cache hit for ICAO: {}", icaoCode);
                            return info;
                        })
                        .or(() -> {
                            cacheMissCounter.increment();
                            return Optional.empty();
                        })
        ).orElse(Optional.empty());
    }

    public void put(String icaoCode, AirportInfo info) {
        executeCacheOperation("put", () -> {
            getCache().ifPresent(cache -> {
                cache.put(normalizeKey(icaoCode), info);
                log.debug("Cached airport info for ICAO: {}", icaoCode);
            });
            return null;
        });
    }

    private <T> Optional<T> executeCacheOperation(String operationName, java.util.function.Supplier<T> operation) {
        try {
            return Optional.ofNullable(operation.get());
        } catch (Exception e) {
            log.warn("Cache {} operation failed, continuing without cache | {}: {}",
                    operationName, e.getClass().getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Cache> getCache() {
        return Optional.ofNullable(cacheManager.getCache(CACHE_NAME));
    }

    private String normalizeKey(String icaoCode) {
        return icaoCode.toUpperCase();
    }
}