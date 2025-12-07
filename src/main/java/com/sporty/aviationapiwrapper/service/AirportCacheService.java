package com.sporty.aviationapiwrapper.service;

import com.sporty.aviationapiwrapper.dto.AirportInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirportCacheService {

    private static final String CACHE_NAME = "airports";

    private final CacheManager cacheManager;

    public Optional<AirportInfo> get(String icaoCode) {
        return getCache()
                .flatMap(cache -> Optional.ofNullable(cache.get(normalizeKey(icaoCode), AirportInfo.class)))
                .map(info -> {
                    log.debug("Cache hit for ICAO: {}", icaoCode);
                    return info;
                });
    }

    public void put(String icaoCode, AirportInfo info) {
        getCache().ifPresent(cache -> {
            cache.put(normalizeKey(icaoCode), info);
            log.debug("Cached airport info for ICAO: {}", icaoCode);
        });
    }

    private Optional<Cache> getCache() {
        return Optional.ofNullable(cacheManager.getCache(CACHE_NAME));
    }

    private String normalizeKey(String icaoCode) {
        return icaoCode.toUpperCase();
    }
}