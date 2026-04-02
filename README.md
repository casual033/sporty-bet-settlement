# Settlement Feed Standardization Service

A Spring Boot microservice that acts as a standardization layer in a sports betting feed processing pipeline.

## Purpose

Sports betting platforms ingest real-time data from multiple third-party feed providers, each delivering
messages in their own proprietary format. This service normalizes incoming feed data from different providers
into a consistent internal format, enabling downstream systems to work with a unified message structure
regardless of the original source.

## Supported Message Types

- **Odds Change** — updates to the 1X2 market odds (home win / draw / away win) for a given event
- **Bet Settlement** — the final match outcome for an event

## Providers

| Provider | Endpoint |
|----------|----------|
| ProviderAlpha | `POST /provider-alpha/feed` |
| ProviderBeta | `POST /provider-beta/feed` |

## Prerequisites

- Java 17+

## Build

```bash
./mvnw clean package
```

## Run

```bash
./mvnw spring-boot:run
```

The service starts on `http://localhost:8080`.

## Test

```bash
./mvnw test
```

## Example Requests

### ProviderAlpha — Odds Update

```bash
curl -X POST http://localhost:8080/provider-alpha/feed \
  -H "Content-Type: application/json" \
  -d '{
    "msg_type": "odds_update",
    "event_id": "ev123",
    "values": { "1": 2.0, "X": 3.1, "2": 3.8 }
  }'
```

### ProviderAlpha — Settlement

```bash
curl -X POST http://localhost:8080/provider-alpha/feed \
  -H "Content-Type: application/json" \
  -d '{
    "msg_type": "settlement",
    "event_id": "ev123",
    "outcome": "1"
  }'
```

### ProviderBeta — Odds Update

```bash
curl -X POST http://localhost:8080/provider-beta/feed \
  -H "Content-Type: application/json" \
  -d '{
    "type": "ODDS",
    "event_id": "ev456",
    "odds": { "home": 1.95, "draw": 3.2, "away": 4.0 }
  }'
```

### ProviderBeta — Settlement

```bash
curl -X POST http://localhost:8080/provider-beta/feed \
  -H "Content-Type: application/json" \
  -d '{
    "type": "SETTLEMENT",
    "event_id": "ev456",
    "result": "away"
  }'
```

## Architecture

The service follows a layered architecture:

1. **Controllers** receive raw JSON from each provider
2. **Mappers** convert provider-specific DTOs into a normalized internal model
3. **Service** orchestrates the mapping and publishes the result
4. **Publisher** sends normalized messages to a message queue (in-memory mock for this exercise)
