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
