# koki-tracking-server

Spring Boot microservice that ingests, enriches, and persists tracking events from across the Koki platform, providing
real-time event processing with modular enrichment pipelines and scheduled KPI aggregation for analytics and reporting.

[![koki-tracking-server-master](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml)
[![koki-tracking-server-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml)
[![Code Coverage](../../.github/badges/koki-tracking-server-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml)

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [High-Level System Diagram](#high-level-system-diagram)
- [Usage Examples](#usage-examples)
- [License](#license)

## Features

- **Event Ingestion**: Consumes tracking events from RabbitMQ queues published by Koki platform components (servers,
  portals, chatbots, SDKs)
- **Modular Enrichment Pipeline**: Sequential filter-based architecture with pluggable filters for extensible event
  processing
- **Bot Detection**: Identifies and flags bot traffic using UAParser library for user-agent analysis
- **Traffic Source Attribution**: Determines referral sources (direct, social media, search engines, email campaigns,
  etc.)
- **Device Classification**: Categorizes devices as desktop, mobile, tablet, or unknown based on user-agent parsing
- **Geographic Resolution**: Resolves IP addresses to country codes using GeoIP services with Redis caching
- **Batch Persistence**: Buffered CSV-based storage of enriched events to local filesystem or AWS S3
- **KPI Aggregation**: Scheduled jobs generate daily and monthly KPI listings (impressions, views, clicks, messages,
  unique visitors) per product
- **Dead Letter Queue (DLQ) Processing**: Automatic retry mechanism for failed message processing with configurable
  retry limits
- **Redis Caching**: High-performance caching for GeoIP lookups and reference data to minimize external API calls
- **Spring Boot Actuator**: Health checks, metrics, and monitoring endpoints for operational visibility
- **OpenAPI Documentation**: Auto-generated API documentation accessible via Swagger UI
- **Scheduled Jobs**: Cron-based job scheduling for buffer flushing, KPI generation, and DLQ reprocessing
- **Multi-Storage Support**: Pluggable storage providers (local filesystem or AWS S3) for event and KPI persistence
- **Async Processing**: Non-blocking event processing with Spring's async capabilities
- **Scalable Architecture**: Stateless design allows horizontal scaling of consumer instances

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple?logo=kotlin)
![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Frameworks

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring-Security-green?logo=springsecurity)
![Spring Cache](https://img.shields.io/badge/Spring-Cache-green?logo=spring)

### Databases & Caching

![Redis](https://img.shields.io/badge/Redis-7.0+-red?logo=redis)

### Cloud & Storage

![AWS S3](https://img.shields.io/badge/AWS-S3-orange?logo=amazons3)

### Tools & Libraries

![Maven](https://img.shields.io/badge/Maven-3.8+-red?logo=apachemaven)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0+-orange?logo=rabbitmq)
![Apache Commons CSV](https://img.shields.io/badge/Apache-Commons%20CSV-blue)
![UAParser](https://img.shields.io/badge/UAParser-Java-lightgrey)
![SpringDoc OpenAPI](https://img.shields.io/badge/SpringDoc-OpenAPI-green)

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
└── pom.xml                                       # Maven configuration
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
```

**Data Flow:**

1. Event producers publish TrackSubmittedEvent to RabbitMQ
2. TrackingConsumer receives events from the queue
3. Events pass through the enrichment pipeline sequentially
4. Each filter enriches the event (bot detection, source, device, geo)
5. PersisterFilter buffers events and flushes to CSV storage
6. Scheduled jobs read CSV files and generate KPI aggregations
7. Redis caches frequently accessed data (GeoIP lookups)
8. Failed messages are routed to DLQ for retry processing

## Usage Examples

### Event Publishing (Producer Side)

```kotlin
import com.wutsi.koki.track.dto.PushTrackRequest
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.ChannelType
import org.springframework.amqp.rabbit.core.RabbitTemplate

// From koki-server or other producer service
class TrackingService(private val rabbitTemplate: RabbitTemplate) {

    fun trackPageView(
        productId: String,
        page: String,
        deviceId: String,
        userAgent: String,
        ipAddress: String,
        referrer: String?
    ) {
        val request = PushTrackRequest(
            time = System.currentTimeMillis(),
            deviceId = deviceId,
            productId = productId,
            event = TrackEvent.PAGE_VIEW,
            page = page,
            ua = userAgent,
            ip = ipAddress,
            referrer = referrer,
            channelType = ChannelType.WEB
        )

        rabbitTemplate.convertAndSend(
            "koki-tracking",
            "koki-tracking-queue",
            request
        )
    }
}
```

### Enrichment Pipeline Configuration

```kotlin
import com.wutsi.koki.tracking.server.service.Pipeline
import com.wutsi.koki.tracking.server.service.filter.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PipelineConfiguration {

    @Bean
    fun trackingPipeline(
        botFilter: BotFilter,
        sourceFilter: SourceFilter,
        deviceTypeFilter: DeviceTypeFilter,
        countryFilter: CountryFilter,
        persisterFilter: PersisterFilter
    ): Pipeline {
        return Pipeline(
            steps = listOf(
                botFilter,           // Step 1: Detect bots
                sourceFilter,        // Step 2: Determine traffic source
                deviceTypeFilter,    // Step 3: Classify device type
                countryFilter,       // Step 4: Resolve geo location
                persisterFilter      // Step 5: Buffer and persist
            )
        )
    }
}
```

### Custom Filter Implementation

```kotlin
import com.wutsi.koki.tracking.server.domain.TrackEntity
import com.wutsi.koki.tracking.server.service.Filter
import org.springframework.stereotype.Service

@Service
class CustomEnrichmentFilter : Filter {

    override fun filter(track: TrackEntity): TrackEntity {
        // Custom enrichment logic
        val customValue = extractCustomData(track)

        return track.copy(
            value = customValue,
            // Add any other custom enrichments
        )
    }

    private fun extractCustomData(track: TrackEntity): String? {
        // Implementation details
        return track.url?.let { extractFromUrl(it) }
    }
}
```

### KPI Aggregation Job

```kotlin
import com.wutsi.koki.tracking.server.service.KpiListingGenerator
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth

@Service
class KpiListingGeneratorJob(
    private val kpiGenerator: KpiListingGenerator
) {

    @Scheduled(cron = "\${koki.kpi.listing.daily-cron}")
    fun generateDailyKpis() {
        // Generate daily KPIs for all listings
        val date = LocalDate.now().minusDays(1)
        kpiGenerator.generateDaily(date)
    }

    @Scheduled(cron = "\${koki.kpi.listing.monthly-cron}")
    fun generateMonthlyKpis() {
        // Generate monthly KPIs for all listings
        val month = YearMonth.now().minusMonths(1)
        kpiGenerator.generateMonthly(month)
    }
}
```

### Reading KPI Reports

```kotlin
import com.wutsi.koki.tracking.server.dao.KpiListingRepository
import com.wutsi.koki.tracking.server.domain.KpiListingEntity
import java.time.LocalDate

class KpiReportService(
    private val kpiRepository: KpiListingRepository
) {

    fun getDailyKpis(date: LocalDate): List<KpiListingEntity> {
        // Read daily KPI report from storage
        return kpiRepository.findByDate(date)
    }

    fun getProductKpis(
        productId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<KpiListingEntity> {
        // Get KPIs for specific product over date range
        return kpiRepository.findByProductAndDateRange(productId, startDate, endDate)
    }
}
```

### Monitoring and Health Checks

```bash
# Check service health
curl http://localhost:8083/actuator/health

# View metrics
curl http://localhost:8083/actuator/metrics

# View specific metric (e.g., event processing rate)
curl http://localhost:8083/actuator/metrics/tracking.events.processed

# Access Swagger UI for API documentation
open http://localhost:8083/swagger-ui.html
```

### Event CSV Format

Enriched events are persisted in CSV format with the following structure:

```csv
time,correlationId,deviceId,accountId,tenantId,productId,ua,bot,ip,lat,long,referrer,page,component,event,value,url,source,campaign,channelType,deviceType,country,rank
1699920000000,abc-123,device-456,acc-789,1,listing-123,"Mozilla/5.0...",false,192.168.1.1,40.7128,-74.0060,"https://google.com","/listings/123",search-results,PAGE_VIEW,,https://koki.com/listings/123,google,,WEB,DESKTOP,US,1
```

### DLQ Processing

Failed messages are automatically routed to the dead letter queue and retried:

```yaml
# Configure DLQ processing
koki:
    module:
        tracking:
            mq:
                dlq-cron: "0 */15 * * * *"  # Retry every 15 minutes
                max-retries: 24               # Maximum retry attempts

wutsi:
    platform:
        mq:
            rabbitmq:
                max-retries: 24
                ttl-seconds: 84600            # 23.5 hours before expiry
```

## License

This project is licensed under the MIT License. See the [LICENSE.md](../../LICENSE.md) file for details.

