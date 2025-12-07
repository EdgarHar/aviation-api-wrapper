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

    @Bean
    @Primary
    public AirportDataProvider compositeAirportDataProvider(
            @Qualifier("aviationApiProvider") AirportDataProvider primary,
            @Qualifier("apiNinjasProvider") AirportDataProvider fallback) {
        return new CompositeAirportDataProvider(List.of(primary, fallback));
    }

}