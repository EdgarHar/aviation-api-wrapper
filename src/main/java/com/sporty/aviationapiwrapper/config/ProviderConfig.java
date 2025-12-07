package com.sporty.aviationapiwrapper.config;

import com.sporty.aviationapiwrapper.provider.AirportDataProvider;
import com.sporty.aviationapiwrapper.provider.CompositeAirportDataProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class ProviderConfig {

    /**
     * Creates a composite airport data provider that chains multiple providers with fallback support.
     * Providers are tried in order: first Aviation API, then API Ninjas if the primary fails.
     *
     * @param primary the primary provider (Aviation API)
     * @param fallback the fallback provider (API Ninjas)
     * @return composite provider that tries providers in sequence until one succeeds
     */
    @Bean
    @Primary
    public AirportDataProvider compositeAirportDataProvider(
            @Qualifier("aviationApiProvider") AirportDataProvider primary,
            @Qualifier("apiNinjasProvider") AirportDataProvider fallback) {
        return new CompositeAirportDataProvider(List.of(primary, fallback));
    }

}