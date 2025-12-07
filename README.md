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
curl http://localhost:8080/api/airports/KJFK
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

## Stop Everything

```bash
docker-compose down
```

Remove all data:
```bash
docker-compose down -v
```