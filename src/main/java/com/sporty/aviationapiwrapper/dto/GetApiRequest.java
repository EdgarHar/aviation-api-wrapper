package com.sporty.aviationapiwrapper.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class GetApiRequest<T> {
    private String url;
    private Map<String, String> pathVariables;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private Class<T> responseType;
}