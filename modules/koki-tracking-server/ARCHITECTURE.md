# ARCHITECTURE

## Table of Content

- [Overview](#overview)
- [Project Structure](#project-structure)
- [High-Level System Diagram](#high-level-system-diagram)
- [Core Components](#core-components)
    - [Application Bootstrap](#application-bootstrap)
    - [Configuration](#configuration)
    - [TrackingConsumer](#trackingconsumer)
    - [Pipeline](#pipeline)
    - [Filters](#filters)
    - [Repositories](#repositories)
    - [KpiListingGenerator](#kpilistinggenerator)
    - [Scheduled Jobs](#scheduled-jobs)
    - [Domain Models](#domain-models)
- [Data Stores](#data-stores)
- [Deployment & Infrastructure](#deployment--infrastructure)
- [Security Considerations](#security-considerations)

## Overview

This document describes the architecture of the **koki-tracking-server** module. The service ingests tracking events (
page views, impressions, clicks, messages) emitted by other Koki platform components (servers, portals, chatbots, SDKs),
enriches them through a configurable pipeline, persists batched raw events as CSV assets, and generates aggregated KPI
listings for analytics. The current implementation uses a file/object storage strategy (local or S3) rather than a
relational database.

## Project Structure

```
modules/koki-tracking-server/
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/wutsi/koki/tracking/server/
    │   │       ├── config/
    │   │       ├── service/
    │   │       │   └── filter/
    │   │       ├── dao/
    │   │       ├── domain/
    │   │       ├── job/
    │   └── resources/
    └── test/
```

## High-Level System Diagram

```
Producers (Portal / Server / Chatbots / SDK)
        |
        v
  RabbitMQ Exchange/Queue (tracking)
        |
        v
  TrackingConsumer
        |
        v
  Pipeline (ordered Filters)
        |  BotFilter → SourceFilter → DeviceTypeFilter → CountryFilter → PersisterFilter
        v
  CSV Assets (Raw Track Batches) in local/S3 storage
        |
        v
  Scheduled Jobs (KPI Listing Generation / Buffer Flush)
        |
        v
  CSV Assets (Aggregated KPI Listings)
        |
        v
  Downstream Analytics / Reporting
```

## Core Components

### Application Bootstrap

**Purpose:** Entry point launching the Spring Boot context and enabling async processing, scheduling, and caching.
**Key Functions:** Initializes component scanning and runtime infrastructure annotations.
**Interactions:** Other components are auto-configured and injected after context startup.

### Configuration

**Purpose:** Declares bean wiring for pipeline filters and messaging consumer settings.
**Key Functions:** Assembles the ordered `Pipeline` list; defines RabbitMQ exchange/queue names and consumer delay
parameters.
**Interactions:** Supplies constructed `Pipeline` to `TrackingConsumer`; provides configuration values to filters via
property injection.

### TrackingConsumer

**Purpose:** Consumes `TrackSubmittedEvent` messages from RabbitMQ.
**Key Functions:** Deserializes event payload, logs key-value diagnostics, expands multi-product events into
multiple `TrackEntity` instances.
**Interactions:** Invokes `Pipeline.filter` for each produced entity; interacts with logging service for observability.

### Pipeline

**Purpose:** Orchestrates sequential transformation/enrichment of a `TrackEntity` through registered filters.
**Key Functions:** Iterates over `Filter` implementations; passes transformed entity between steps.
**Interactions:** Used by `TrackingConsumer`; delegates enrichment logic to filter components.

### Filters

**Purpose:** Apply discrete enrichment or classification operations to a tracking event.
**Components & Functions:**

- **BotFilter:** Parses `ua` to flag bots (`bot=true`).
- **SourceFilter:** Uses URL/referrer/UA to detect traffic source; sets `source` when available.
- **DeviceTypeFilter:** Classifies device category (MOBILE/TABLET/DESKTOP/UNKNOWN).
- **CountryFilter:** Resolves ISO country code from IP; leaves unchanged if resolution fails.
- **PersisterFilter:** Buffers events and triggers batch CSV persistence via `TrackRepository` at threshold.
  **Interactions:** Executed in a fixed order inside the `Pipeline`.

### Repositories

**Purpose:** Persist and retrieve CSV-based representations of raw tracking events and aggregated KPI listings.
**Components & Functions:**

- **TrackRepository:** Writes raw events to date-partitioned CSV files (daily/monthly folder hierarchy).
- **KpiListingRepository:** Writes aggregated KPI listing records to month-partitioned CSV files.
  **Interactions:** Called by `PersisterFilter` (raw events) and `KpiListingGenerator` (KPI listings).
  Uses `StorageService` abstraction for local/S3 storage operations.

### KpiListingGenerator

**Purpose:** Aggregates raw tracking events into KPI listing metrics.
**Key Functions:** Loads monthly track CSV assets, filters out bots, groups by product, counts
impressions/views/clicks/messages/unique visitors.
**Interactions:** Reads via `TrackRepository.read`; writes aggregated output through `KpiListingRepository`.

### Scheduled Jobs

**Purpose:** Automate periodic batch operations.
**Key Functions:** Flush buffered raw events; trigger KPI listing generation on defined cron schedules.
**Interactions:** Invoke `PersisterFilter.flush` and `KpiListingGenerator.generate` routines.

### Domain Models

**Purpose:** Represent enriched raw events and aggregated KPI results.
**Key Models:** `TrackEntity` (raw event with enrichment attributes), `KpiListingEntity` (aggregated KPI counts per
product).
**Interactions:** Passed through filters, persisted by repositories, aggregated by generator.

## Data Stores

- **Object/Local Storage (S3 or filesystem):** CSV persistence for raw tracking events and KPI listings.
- **In-Memory Buffer:** Temporary event accumulation inside PersisterFilter.
- **RabbitMQ:** Message transport for incoming tracking events.

## Deployment & Infrastructure

Build:

```bash
mvn clean install
```

Artifact: Executable JAR `koki-tracking-server-VERSION_NUMBER`.

Profiles:

- local: frequent cron, local storage, verbose logging.
- test: controlled consumer timing.
- prod: hourly aggregation, S3 storage.

Key Configuration:

```yaml
koki.persister.buffer-size: 10000
koki.persister.cron: "0 */15 * * * *"
koki.kpi.listing.daily-cron: "0 */15 * * * *"
koki.kpi.listing.monthly-cron: "0 30 5 2 * *"
wutsi.platform.mq.rabbitmq.url: amqp://[RABBIT_HOST]
wutsi.platform.storage.type: local | s3
wutsi.platform.cache.redis.url: redis://[REDIS_HOST]:6379
```

Scaling:

- Multiple consumer instances on the same queue.
- Batch persistence reduces write pressure.

Observability:

- Structured logging (key/value).
- Actuator endpoints (health, info, scheduled tasks).

Resilience:

- Buffered writes decouple ingestion from storage latency.
- RabbitMQ handles transient delivery concerns.

## Security Considerations

- **Authentication/Authorization:** Spring Security available (mechanism not defined here).
- **Validation:** Bean validation + enum coercion.
- **Multi-Tenancy:** Tenant id and product id attributes scope aggregation.
- **Privacy:** Limited identifiers (device/account); avoid PII.
- **Integrity:** Bot flag for exclusion; multi-product expansion preserves ordering.
- **Transport:** Recommend TLS for RabbitMQ and S3 in production.
- **Least Privilege:** Storage and queue access scoped to required operations.
- **Hardening:** Rate limiting, DLQ reprocessing automation, audit logging, encryption at rest (S3 policies) can be
  added.
