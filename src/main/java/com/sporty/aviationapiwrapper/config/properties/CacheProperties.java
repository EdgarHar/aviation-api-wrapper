package com.sporty.aviationapiwrapper.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    private Duration defaultTtl = Duration.ofMinutes(10);
    private Duration airportsTtl = Duration.ofMinutes(15);
}