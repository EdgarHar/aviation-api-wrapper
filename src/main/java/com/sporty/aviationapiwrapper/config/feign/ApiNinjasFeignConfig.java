package com.sporty.aviationapiwrapper.config.feign;

import feign.Logger;
import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class ApiNinjasFeignConfig {

    @Value("${api.providers.apiNinjas.timeout:5000}")
    private int timeout;

    @Bean("apiNinjasRequestOptions")
    public Request.Options requestOptions() {
        return new Request.Options(
            timeout, TimeUnit.MILLISECONDS,
            timeout, TimeUnit.MILLISECONDS,
            true
        );
    }

    @Bean("apiNinjasFeignLoggerLevel")
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}