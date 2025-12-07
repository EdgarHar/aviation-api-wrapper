package com.sporty.aviationapiwrapper.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Component
@ConfigurationProperties(prefix = "api")
public class ApiProperties {
    private Map<String, ProviderProperties> providers = new HashMap<>();

    public ProviderProperties getProvider(String name) {
        return Optional.ofNullable(providers.get(name))
                .orElseThrow(() -> new IllegalArgumentException("Provider not found: " + name)); //change to another type
    }
}