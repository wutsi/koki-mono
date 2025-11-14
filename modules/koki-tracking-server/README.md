# koki-tracking-server

A Kotlin Spring Boot service that ingests, enriches, and persists tracking events produced across the Koki platform.

![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml/badge.svg)

![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml/badge.svg)

![JaCoCo](../../.github/badges/koki-tracking-server-jacoco.svg)

![Java](https://img.shields.io/badge/Java-17-blue)

![Kotlin](https://img.shields.io/badge/Kotlin-language-purple)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)

![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0-orange)

![Redis](https://img.shields.io/badge/Redis-7.0-red)

## Table of Contents

- [About the Project](#about-the-project)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Project](#running-the-project)
    - [Running Tests](#running-tests)
- [License](#license)

## About the Project

The **koki-tracking-server** collects tracking events (page views, impressions, interactions) emitted by other Koki
components (servers, portals, chatbots, SDKs). It applies a modular enrichment pipeline (bot filtering, source
attribution, device type and geo classification) and produces enriched event data plus aggregated KPI metrics for
downstream analytics and reporting. This enables a unified, scalable approach to understanding user engagement across
multiple channels without requiring a full streaming analytics stack.

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
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-tracking-server
```

2. Build the project:

```bash
mvn clean install
```

3. Configure the application by creating or editing **application-local.yml**:

```yaml
wutsi:
    platform:
        mq:
            rabbitmq:
                url: amqp://localhost
        cache:
            redis:
                url: redis://localhost:6379
        storage:
            type: local
            local:
                directory: ${user.home}/__wutsi
```

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

### Running Tests

Execute unit tests:

```bash
mvn test
```

Run all tests including integration tests:

```bash
mvn verify
```

Generate test coverage report:

```bash
mvn clean test jacoco:report
```

The coverage report will be available at **target/site/jacoco/index.html**.

## License

This project is licensed under the MIT License. See [LICENSE.md](../../LICENSE.md) for details.

