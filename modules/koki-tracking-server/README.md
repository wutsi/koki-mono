# koki-tracking-server

A Spring Boot Kotlin service that ingests, processes, and persists tracking events (page views, impressions,
interactions) for analytics and KPI generation across the Koki platform.

[![koki-tracking-server CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml)

[![koki-tracking-server CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml)

![Coverage](../../.github/badges/koki-tracking-server-jococo.svg)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

![MySQL](https://img.shields.io/badge/Database-MySQL-blue.svg)

## About the Project

`koki-tracking-server` collects user and system interaction events emitted by other Koki services or clients. Events
enter a processing pipeline that normalizes, expands (splitting multi-product impressions), enriches with device/user
metadata, and persists them for downstream KPI room generation, reporting, and analytics. High test coverage ensures
reliability of the pipeline and transformation logic.

### Features

- **Event Ingestion Pipeline** – Consumer based processing with filtering stages for validation, expansion, and
  persistence.
- **Multi-Product Impression Handling** – Automatic splitting of impression events with ranked product identifiers.
- **Scheduling & KPI Generation** – Cron-driven jobs produce daily and monthly KPI rollups (configurable frequency).
- **Message Queue Integration** – RabbitMQ exchange with primary queue + dead letter queue retry strategy.
- **High Coverage & Observability** – Actuator endpoints, structured logging (KVLogger), and high Jacoco targets.

## Getting Started

Run the tracking server locally to begin consuming tracking events.

### Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **MySQL 8+** (password-less root recommended for local)
- **RabbitMQ** (local broker on `amqp://localhost`)
- **Redis** (optional cache if enabled by platform config)

### 1. Clone

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-tracking-server
```

### 2. Create Database

```bash
mysql -u root <<'SQL'
CREATE DATABASE IF NOT EXISTS koki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SQL
```

### 3. Configure (Optional Local Override)

Create `application-local.yml` for custom overrides:

```yaml
server:
    port: 8083
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/koki?serverTimezone=UTC
        username: root
        password: ""
wutsi:
    platform:
        mq:
            rabbitmq:
                url: amqp://localhost
        cache:
            type: redis
            redis:
                url: redis://:test@localhost:6379
```

Run with profile:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

### 4. Build

```bash
mvn clean install
```

### 5. Run

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/koki-tracking-server-VERSION_NUMBER.jar
```

Service default URL: `http://localhost:8083`

### 6. Health & Info

```bash
curl http://localhost:8083/actuator/health
curl http://localhost:8083/actuator/info
```

### 7. Submitting a Tracking Event (Example)

Events are published to the queue by other services; to simulate you can send an HTTP POST if an ingestion endpoint
exists or publish directly via RabbitMQ CLI (replace payload accordingly). If no HTTP endpoint is exposed, use the
queue:

```bash
# Pseudo example using rabbitmqadmin (adjust if installed)
rabbitmqadmin publish exchange=koki-tracking routing_key=koki-tracking-queue payload='{"event":"IMPRESSION","tenantId":1,"productId":"123|456","time":"2025-01-01T10:00:00Z"}'
```

### 8. Logs

Structured key-value logs include fields such as `track_event`, `track_product_id`, `track_correlation_id`, enabling
downstream log aggregation.

### 9. Cron Configuration

Default cron expressions (see `application.yml`):

- Daily room KPI flush: `0 */15 * * * *`
- Monthly KPI generation: `0 30 5 2 * *`
- Persister flush: `0 */15 * * * *`
- DLQ processing: `0 */15 * * * *`
  Adjust by overriding properties under `koki.kpi.room` and `koki.persister`.

### 10. Troubleshooting

| Issue               | Possible Cause                                  | Resolution                                                    |
|---------------------|-------------------------------------------------|---------------------------------------------------------------|
| No events processed | Queue empty / broker down                       | Verify RabbitMQ running and exchange/queue names match config |
| High DLQ size       | Malformed events or transient failures          | Inspect DLQ messages; increase retries or fix producer schema |
| Slow persistence    | DB saturation or large batch size               | Tune `koki.persister.buffer-size` and DB connection pool      |
| Missing rankings    | IMPRESSION events not using product pipe format | Ensure producers send `productId` as `id1                     |id2|...` |

## License

See the root [License](../../LICENSE.md).

