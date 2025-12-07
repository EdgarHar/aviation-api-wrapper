package com.sporty.aviationapiwrapper.provider.aviationapi;

import com.sporty.aviationapiwrapper.config.feign.AviationApiFeignConfig;
import com.sporty.aviationapiwrapper.dto.AviationApiAirportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "aviationApi",
    url = "${api.providers.aviationApi.baseUrl}",
    configuration = AviationApiFeignConfig.class
)
public interface AviationApiFeignClient {

    @GetMapping("${api.providers.aviationApi.endpoints.airports}")
    AviationApiAirportResponse getAirport(@RequestParam("apt") String icaoCode);
}