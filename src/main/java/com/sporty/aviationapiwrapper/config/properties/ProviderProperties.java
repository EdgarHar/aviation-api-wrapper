package com.sporty.aviationapiwrapper.config.properties;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ProviderProperties {
    private String baseUrl;
    private String apiKey;
    private int timeout = 5000;
    private Map<String, String> endpoints = new HashMap<>();
}