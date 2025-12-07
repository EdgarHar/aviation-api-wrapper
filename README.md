# Aviation API Wrapper

A resilient Spring Boot wrapper service for aviation data APIs with caching, circuit breakers, retries, and monitoring.

## Features

- **Multiple Provider Support**: Integrates with AviationAPI and API Ninjas with automatic failover
- **Resilience Patterns**:
  - Circuit breakers to prevent cascading failures
  - Automatic retries with exponential backoff
  - Request timeouts
- **Redis Caching**: Fast responses with 15-minute TTL
- **Monitoring Stack**:
  - Prometheus metrics
  - Pre-configured Grafana dashboards
  - Custom cache hit/miss metrics
- **Health Checks**: Circuit breaker states and system health

## Prerequisites

- Java 21
- Docker & Docker Compose
- Maven 3.8+

## Quick Start

### 1. Start Infrastructure

```bash
docker-compose up -d
```

This starts Redis (6379), Prometheus (9090), and Grafana (3000).

### 2. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at **http://localhost:8080**

## Usage

### Get Airport Information

```bash
curl http://localhost:8080/api/v1/airports/KJFK
```

Response:
```json
{
  "icao": "KJFK",
  "name": "John F Kennedy International Airport",
  "city": "New York",
  "country": "United States"
}
```

### Access Monitoring

- **Grafana Dashboards**: http://localhost:3000 (admin/admin)
  - Aviation API Demo Dashboard
  - Airport Cache Metrics
- **Prometheus**: http://localhost:9090
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus

## Running Tests

Run all tests (unit + integration):
```bash
mvn test
```

## Architecture

### System Design

```
┌─────────┐      ┌──────────────────┐      ┌──────────────┐
│ Client  │─────▶│ AirportController│─────▶│AirportService│
└─────────┘      └──────────────────┘      └──────┬───────┘
                                                   │
                                    ┌──────────────┴──────────────┐
                                    │                             │
                           ┌────────▼──────────┐    ┌────────────▼────────────┐
                           │AirportCacheService│    │CompositeAirportProvider │
                           └───────────────────┘    └────────────┬────────────┘
                                                                  │
                                            ┌─────────────────────┴─────────────────┐
                                            │                                       │
                                   ┌────────▼─────────┐                   ┌─────────▼────────┐
                                   │AviationApiProvider│                  │ApiNinjasProvider │
                                   └────────┬─────────┘                   └─────────┬────────┘
                                            │                                       │
                                   ┌────────▼─────────┐                   ┌─────────▼────────┐
                                   │AviationApiClient │                   │ApiNinjasClient   │
                                   └────────┬─────────┘                   └─────────┬────────┘
                                            │                                       │
                                   ┌────────▼─────────┐                   ┌─────────▼────────┐
                                   │Feign Client      │                   │Feign Client      │
                                   │+ Circuit Breaker │                   │+ Circuit Breaker │
                                   │+ Retry           │                   │+ Retry           │
                                   └────────┬─────────┘                   └─────────┬────────┘
                                            │                                       │
                                   ┌────────▼─────────┐                   ┌─────────▼────────┐
                                   │AviationAPI.com   │                   │API Ninjas        │
                                   └──────────────────┘                   └──────────────────┘
```

### Key Components

**Controller Layer**
- `AirportController`: REST endpoint for airport lookups

**Service Layer**
- `AirportService`: Orchestrates cache and provider calls
- `AirportCacheService`: Manages Redis caching with metrics

**Provider Layer**
- `CompositeAirportDataProvider`: Implements failover between providers
- `AviationApiProvider`: Primary provider adapter
- `ApiNinjasProvider`: Fallback provider adapter

**Client Layer**
- `AviationApiClient`: HTTP client for AviationAPI.com
- `ApiNinjasClient`: HTTP client for API Ninjas
- Both use Feign clients wrapped with Resilience4j circuit breakers and retry mechanisms

**Infrastructure**
- Redis for distributed caching
- Prometheus for metrics collection
- Grafana for visualization

### Resilience Patterns

**Circuit Breaker**
- Failure rate threshold: 50%
- Minimum calls: 5
- Sliding window: 10 calls
- Wait duration in open state: 20-30s
- Automatic transition to half-open state

**Retry Mechanism**
- Max attempts: 2-3 (provider-specific)
- Exponential backoff: 2x multiplier
- Max wait: 2s
- Retries on: IOException, TimeoutException, FeignException

**Caching**
- TTL: 15 minutes
- Cache hit/miss metrics tracked via Micrometer

### Provider Failover Strategy

The service attempts providers in order:
1. AviationAPI (primary, no API key required)
2. API Ninjas (fallback, requires API key)

If both fail, returns 404 with error details.

## Assumptions

1. **Third-party API Availability**: Both aviation APIs are public and may be unstable or rate-limited
2. **ICAO Code Format**: Expected to be 4-character alphanumeric codes, automatically converted to uppercase
3. **Caching Strategy**: 15-minute TTL is sufficient for airport data (rarely changes)
4. **No Authentication**: No user management or authentication required for this wrapper service
5. **Environment**: Production deployment would use environment variables for API keys and Redis connection
6. **Scalability**: Stateless design allows horizontal scaling behind a load balancer
7. **Monitoring**: Metrics are exposed but alerting rules would be configured externally

## Error Handling

**Client Errors (4xx)**
- `400 Bad Request`: Invalid ICAO code format
- `404 Not Found`: Airport not found in any provider

**Server Errors (5xx)**
- `503 Service Unavailable`: All providers failed or circuit breakers open
- Circuit breakers prevent cascading failures
- Fallback methods return empty responses with logging

**Resilience Mechanisms**
- Timeouts prevent hanging requests (5s per provider)
- Circuit breakers protect against failing dependencies
- Retries handle transient failures

## AI-Generated Code

AI assistance (Claude) was used for:
- **Test Generation**: Unit test scaffolding for and integration tests
- **Resilience4j Configuration**: Initial circuit breaker and retry parameters in `application.yml`
- **Grafana Dashboard JSON**: Prometheus query syntax and panel configurations
- **Javadoc**: Generated overall javadocs based on code
- **README file**: README file generation