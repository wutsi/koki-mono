# koki-tracking-server

A Kotlin Spring Boot service that ingests, enriches, and persists tracking events produced across the Koki platform.

[![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml)

[![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml)

[![JaCoCo](../../.github/badges/koki-tracking-server-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml)

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Configuration](#configuration)
    - [Running the Project](#running-the-project)
    - [Running Tests](#running-tests)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [High-Level System Diagram](#high-level-system-diagram)
- [License](#license)

## Features

The **koki-tracking-server** collects tracking events (page views, impressions, interactions) emitted by other Koki
components (servers, portals, chatbots, SDKs). It applies a modular enrichment pipeline (bot filtering, source
attribution, device type and geo classification) and produces enriched event data plus aggregated KPI metrics for
downstream analytics and reporting. This enables a unified, scalable approach to understanding user engagement across
multiple channels without requiring a full streaming analytics stack.

**Problem it solves:** Consolidates disparate raw interaction signals into a consistent, enriched dataset and scheduled
KPI aggregates—reducing the need for ad-hoc scripts, manual log scraping, or costly real-time streaming infrastructure
while preserving attribution (source, device, geography) and bot filtering.

- **Event Ingestion**: Consumes tracking events from RabbitMQ queues published by Koki platform components
- **Modular Enrichment Pipeline**: Sequential filter-based architecture for extensible event processing
- **Bot Detection**: Identifies and flags bot traffic using user-agent parsing
- **Traffic Source Attribution**: Determines referral sources (direct, social, search, email, etc.)
- **Device Classification**: Categorizes devices as desktop, mobile, tablet, or unknown
- **Geographic Resolution**: Resolves IP addresses to country codes using GeoIP services
- **Batch Persistence**: Buffered CSV-based storage of raw events to local filesystem or AWS S3
- **KPI Aggregation**: Scheduled jobs generate daily and monthly KPI listings (impressions, views, clicks, messages,
  unique visitors) per product
- **Dead Letter Queue (DLQ) Processing**: Automatic retry mechanism for failed message processing
- **Redis Caching**: Performance optimization for GeoIP lookups and reference data

### Technologies

**Programming Languages**

[![Kotlin](https://img.shields.io/badge/Kotlin-language-purple)](https://kotlinlang.org/) [![Java](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)

**Frameworks**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)](https://spring.io/projects/spring-boot) [![Spring Security](https://img.shields.io/badge/Spring-Security-green)](https://spring.io/projects/spring-security) [![Spring Actuator](https://img.shields.io/badge/Spring-Actuator-green)](https://spring.io/guides/gs/actuator-service)

**Databases**

[![Redis](https://img.shields.io/badge/Redis-7.0+-red)](https://redis.io/)

**Cloud**

[![AWS S3](https://img.shields.io/badge/AWS-S3-orange)](https://aws.amazon.com/s3/)

**Tools & Libraries**

[![Maven](https://img.shields.io/badge/Maven-build-red)](https://maven.apache.org/) [![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0+-orange)](https://www.rabbitmq.com/) [![Apache Commons CSV](https://img.shields.io/badge/Apache-Commons%20CSV-blue)](https://commons.apache.org/proper/commons-csv/) [![UAParser](https://img.shields.io/badge/UA-Parser-lightgrey)](https://github.com/ua-parser/uap-java) [![SpringDoc OpenAPI](https://img.shields.io/badge/SpringDoc-OpenAPI-green)](https://springdoc.org/)

## Getting Started

### Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+**
- **RabbitMQ 4.0+** (for message queue consumption)
- **Redis 7.0+** (for caching GeoIP and reference data)
- **AWS S3** (optional, for production storage) or local filesystem for development

### Installation

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono
```

2. Build the project:

```bash
mvn clean install
```

3. Navigate to the koki-tracking-server module:

```bash
cd modules/koki-tracking-server
```

### Configuration

Configure the application by creating or editing **application-local.yml** in `src/main/resources/`:

```yaml
koki:
    persister:
        buffer-size: 10000                          # Event buffer size before flush
        cron: "0 */15 * * * *"                      # Flush schedule (every 15 mins)

    kpi:
        listing:
            daily-cron: "0 */15 * * * *"            # Daily KPI generation schedule
            monthly-cron: "0 30 5 2 * *"            # Monthly KPI (2nd of month at 5:30 AM)

    module:
        tracking:
            mq:
                consumer-delay-seconds: 1           # Delay between message consumption
                queue: koki-tracking-queue          # Main tracking queue
                dlq: koki-tracking-dlq              # Dead letter queue
                dlq-cron: "0 */15 * * * *"          # Process DLQ every 15 mins

wutsi:
    platform:
        cache:
            type: redis
            redis:
                url: redis://localhost:6379

        mq:
            type: rabbitmq
            rabbitmq:
                url: amqp://localhost
                exchange-name: koki-tracking
                max-retries: 24
                ttl-seconds: 84600

        storage:
            type: local                             # local | s3
            local:
                directory: ${user.home}/__wutsi
            s3:
                bucket: [YOUR_S3_BUCKET]
                region: [YOUR_AWS_REGION]
                access-key: [YOUR_ACCESS_KEY]
                secret-key: [YOUR_SECRET_KEY]
```

**Configuration sections:**

- **Persister**: Controls event buffering and flush frequency
- **KPI Generation**: Schedules for daily and monthly aggregations
- **RabbitMQ**: Queue names, DLQ handling, and retry policies
- **Storage**: Choose between local filesystem or S3 for CSV persistence
- **Cache**: Redis configuration for GeoIP and reference data caching

### Running the Project

Run the application locally:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The server will start on port **8083** by default.

Verify the service is running:

```bash
curl http://localhost:8083/actuator/health
```

You can also access the API documentation at:

```
http://localhost:8083/swagger-ui.html
```

### Running Tests

Run all tests:

```bash
mvn test
```

Run tests with coverage report:

```bash
mvn clean verify
```

The JaCoCo coverage report will be generated at `target/site/jacoco/index.html`.

## High-Level Architecture

### Repository Structure

```
modules/koki-tracking-server/
├── src/
│   ├── main/
│   │   ├── kotlin/com/wutsi/koki/tracking/server/
│   │   │   ├── config/                            # Spring configuration beans (pipeline, messaging, scheduling)
│   │   │   ├── service/                           # Business logic services
│   │   │   │   └── filter/                       # Enrichment filter implementations (bot, source, device, country, persister)
│   │   │   ├── dao/                              # Data access layer (CSV repositories for events and KPIs)
│   │   │   ├── domain/                           # Domain entities (TrackEntity, KpiListingEntity)
│   │   │   └── job/                              # Scheduled jobs (persister, KPI generation)
│   │   └── resources/                            # Configuration files (application.yml, profiles)
│   └── test/                                     # Unit and integration tests
├── target/                                       # Build output directory
└── ...                                           # Additional configuration files (pom.xml, Procfile, etc.)
```

**Purpose of each directory:**

- **config/**: Spring configuration classes for pipeline setup, messaging infrastructure, and scheduled task
  configuration
- **service/**: Core business logic including event consumption, enrichment pipeline orchestration, and KPI generation
- **service/filter/**: Modular enrichment filters applied sequentially to each tracking event (bot detection, source
  attribution, device classification, geo resolution, persistence)
- **dao/**: CSV-based repositories for reading/writing raw events and KPI listings to storage (local filesystem or AWS
  S3)
- **domain/**: Entity classes representing tracking events and KPI data structures
- **job/**: Scheduled jobs for buffer flushing, KPI generation, and DLQ reprocessing

### High-Level System Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                      Event Producers                             │
│   (koki-server, koki-portal, koki-portal-public, chatbots)      │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ Publish TrackSubmittedEvent
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                         RabbitMQ                                 │
│                   koki-tracking-queue                            │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ Consume Events
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   koki-tracking-server                           │
│                    TrackingConsumer                              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ Pass through Enrichment Pipeline
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Enrichment Pipeline                           │
│  1. BotFilter          → Detect bot traffic                      │
│  2. SourceFilter       → Determine traffic source                │
│  3. DeviceTypeFilter   → Classify device type                    │
│  4. CountryFilter      → Resolve IP to country                   │
│  5. PersisterFilter    → Buffer and persist to CSV               │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    ┌────────┴────────┐
                    │                 │
                    ▼                 ▼
        ┌───────────────────┐  ┌──────────────┐
        │   Storage Layer   │  │    Cache     │
        │  (S3 or Local)    │  │   (Redis)    │
        │  - Raw Events     │  │  - GeoIP     │
        │  - KPI Reports    │  │  - RefData   │
        └───────────────────┘  └──────────────┘
                    │
                    │ Scheduled Jobs
                    ▼
        ┌───────────────────────┐
        │  KPI Aggregation      │
        │  - Daily Reports      │
        │  - Monthly Reports    │
        └───────────────────────┘

Data Flow:
1. Event producers publish TrackSubmittedEvent to RabbitMQ
2. TrackingConsumer receives events from the queue
3. Events pass through the enrichment pipeline sequentially
4. Each filter enriches the event (bot detection, source, device, geo)
5. PersisterFilter buffers events and flushes to CSV storage
6. Scheduled jobs read CSV files and generate KPI aggregations
7. Redis caches frequently accessed data (GeoIP lookups)
8. Failed messages are routed to DLQ for retry processing
```

## License

This project is licensed under the MIT License. See [LICENSE.md](../../LICENSE.md) for details.

