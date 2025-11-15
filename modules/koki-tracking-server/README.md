# koki-tracking-server

A Kotlin Spring Boot service that ingests, enriches, and persists tracking events produced across the Koki platform.

![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml/badge.svg)

![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml/badge.svg)

![Java](https://img.shields.io/badge/Java-17-blue)

![Kotlin](https://img.shields.io/badge/Kotlin-language-purple)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)

![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0-orange)

![Redis](https://img.shields.io/badge/Redis-7.0-red)
[![JaCoCo](../../.github/badges/koki-tracking-server-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml)

- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)

## About the Project

    - [Configuration](#configuration)
    - [Running the Project](#running-the-project)
    - [Running Tests](#running-tests)

- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
      The **koki-tracking-server** collects tracking events (page views, impressions, interactions) emitted by other
      Koki
      components (servers, portals, chatbots, SDKs). It applies a modular enrichment pipeline (bot filtering, source
      attribution, device type and geo classification) and produces enriched event data plus aggregated KPI metrics for
      downstream analytics and reporting. This enables a unified, scalable approach to understanding user engagement
      across
      multiple channels without requiring a full streaming analytics stack.
      downstream analytics and reporting. This enables a unified, scalable approach to understanding user engagement
      across
      multiple channels without requiring a full streaming analytics stack.

**Problem it solves:** Consolidates disparate raw interaction signals into a consistent, enriched dataset and scheduled
KPI aggregates—reducing the need for ad-hoc scripts, manual log scraping, or costly real-time streaming infrastructure
while preserving attribution (source, device, geography) and bot filtering.

- **Batch Persistence**: Buffered CSV-based storage of raw events to local filesystem or AWS S3
- **KPI Aggregation**: Scheduled jobs generate daily and monthly KPI listings (impressions, views, clicks, messages,
  unique visitors) per product

**Tools & Libraries**

[![Maven](https://img.shields.io/badge/Maven-build-red)](https://maven.apache.org/) [![Apache Commons CSV](https://img.shields.io/badge/Apache-Commons%20CSV-blue)](https://commons.apache.org/proper/commons-csv/)

## Getting Started

### Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+**
- **RabbitMQ 4.0+**
- **Redis 7.0+**
- **AWS S3** (for production storage) or local filesystem

### Installation

1. Clone the repository:

```bash
mvn clean install
```

### Configuration

Configure the application by creating or editing **application-local.yml**:

```yaml
koki:
    persister:
        buffer-size: 10000                      # Event buffer size before flush
        cron: "0 */15 * * * *"                  # Flush schedule (every 15 mins)

    kpi:
        listing:
            daily-cron: "0 */15 * * * *"          # Daily KPI generation schedule
            monthly-cron: "0 30 5 2 * *"          # Monthly KPI (2nd of month at 5:30 AM)

    module:
        tracking:
            mq:
                consumer-delay-seconds: 1           # Delay between message consumption
        storage:
            type: local                           # local | s3
            local:
                directory: ${user.home}/__wutsi
            s3:
                bucket: [ YOUR_S3_BUCKET ]
                region: [ YOUR_AWS_REGION ]
                access-key: [ YOUR_ACCESS_KEY ]
                secret-key: [ YOUR_SECRET_KEY ]
```

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
### Running Tests

            redis:
                url: redis://localhost:6379
        storage:
            type: local

```bash
mvn test
```

            type: local

            local:
                directory: ${user.home}/__wutsi

```bash
### Repository Structure

```

modules/koki-tracking-server/
├── src/
│ ├── main/
│ │ ├── kotlin/com/wutsi/koki/tracking/server/
│ │ │ ├── Application.kt # Spring Boot entry point
│ │ │ ├── config/ # Spring configuration beans
│ │ │ ├── service/ # Business logic services
│ │ │ │ ├── TrackingConsumer.kt # RabbitMQ message consumer
│ │ │ │ ├── Pipeline.kt # Enrichment pipeline orchestrator
│ │ │ │ ├── Filter.kt # Filter interface
│ │ │ │ ├── KpiListingGenerator.kt # KPI aggregation service
│ │ │ │ └── filter/ # Enrichment filter implementations
│ │ │ │ ├── BotFilter.kt # Bot detection filter
│ │ │ │ ├── SourceFilter.kt # Traffic source attribution
│ │ │ │ ├── DeviceTypeFilter.kt # Device classification
│ │ │ │ ├── CountryFilter.kt # GeoIP resolution
│ │ │ │ └── PersisterFilter.kt # CSV persistence filter
│ │ │ ├── dao/ # Data access layer (CSV repositories)
│ │ │ ├── domain/ # Domain entities
│ │ │ └── job/ # Scheduled jobs
│ │ └── resources/
│ │ ├── application.yml # Default configuration
│ │ └── application-*.yml # Profile-specific configs
│ └── test/ # Unit and integration tests
├── pom.xml # Maven build configuration
└── ARCHITECTURE.md # Detailed architecture documentation

```

**Purpose of each directory:**

- **config/**: Spring configuration classes for pipeline, messaging, and scheduling setup
This project is licensed under the MIT License. See [LICENSE.md](../../LICENSE.md) for details.

- **service/**: Core business logic including event consumption, enrichment, and KPI generation
- **service/filter/**: Modular enrichment filters applied sequentially to each tracking event
- **dao/**: CSV-based repositories for reading/writing raw events and KPI listings to storage
- **domain/**: Entity classes representing tracking events and KPI data structures
- **job/**: Scheduled jobs for buffer flushing, KPI generation, and DLQ reprocessing

### High-Level System Diagram

```

┌─────────────────────────────────────────────────────────────────┐
│ Event Producers │
│  (koki-server, koki-portal, koki-portal-public, chatbots)      │
└────────────────────────────┬────────────────────────────────────┘
│ Publish TrackSubmittedEvent
▼
