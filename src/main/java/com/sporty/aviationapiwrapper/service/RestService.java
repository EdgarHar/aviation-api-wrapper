package com.sporty.aviationapiwrapper.service;

import com.sporty.aviationapiwrapper.dto.GetApiRequest;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestService {

  private final RestClient restClient;

  public <T> Optional<T> get(GetApiRequest<T> request) {
    return Optional.ofNullable(request)
        .filter(req -> req.getResponseType() != null)
        .flatMap(this::executeGetRequest);
  }

  private <T> Optional<T> executeGetRequest(GetApiRequest<T> request) {
    String url = request.getUrl();
    log.debug("Making GET request to: {}", url);

    try {
      var getSpec = restClient.get();

      var response =
          Optional.of(getSpec)
              .map(
                  spec -> {
                    if (request.getPathVariables() != null) {
                      return spec.uri(url, request.getPathVariables());
                    } else {
                      return spec.uri(
                          uriBuilder -> {
                            var uri = getUri(url);
                            var builder =
                                uriBuilder
                                    .scheme(uri.getScheme())
                                    .host(uri.getHost())
                                    .port(uri.getPort())
                                    .path(uri.getPath());

                            if (request.getQueryParams() != null) {
                              request.getQueryParams().forEach(builder::queryParam);
                            }
                            return builder.build();
                          });
                    }
                  })
              .map(
                  spec ->
                      Optional.ofNullable(request.getHeaders())
                          .map(
                              headers -> {
                                headers.forEach(spec::header);
                                return spec;
                              })
                          .orElse(spec))
              .map(
                  spec -> spec.retrieve().toEntity(request.getResponseType()));

      return response.map(ResponseEntity::getBody);
    } catch (Exception e) {
      log.error("Request failed for URL: {}. Message: {}", url, e.getMessage());
      throw e;
    }
  }

  private URI getUri(String uriString) {
    try {
      return java.net.URI.create(uriString);
    } catch (Exception e) {
      log.error("Failed to parse URI: {}", uriString, e);
      throw new RuntimeException("Invalid URI: " + uriString, e);
    }
  }
}
