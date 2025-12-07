package com.sporty.aviationapiwrapper.provider.apininjas;

import com.sporty.aviationapiwrapper.config.feign.ApiNinjasFeignConfig;
import com.sporty.aviationapiwrapper.provider.apininjas.dto.ApiNinjasAirport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "apiNinjas",
    url = "${api.providers.apiNinjas.baseUrl}",
    configuration = ApiNinjasFeignConfig.class
)
public interface ApiNinjasFeignClient {

    @GetMapping("${api.providers.apiNinjas.endpoints.airports}")
    ApiNinjasAirport[] getAirport(
        @RequestParam("icao") String icaoCode,
        @RequestHeader("X-Api-Key") String apiKey
    );
}