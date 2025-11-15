# koki-portal-public

A Spring Boot web application providing public-facing server-side rendered interfaces for the Koki platform.

![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml/badge.svg)

![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml/badge.svg)

![JaCoCo](../../.github/badges/koki-portal-public-jacoco.svg)

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

The **koki-portal-public** is a public-facing web application that provides customer-facing interfaces for the Koki
platform. Unlike the administrative koki-portal, this module serves end users with access to public listings, file
sharing, reference data, and tracking capabilities. Built with Spring Boot and Thymeleaf, it integrates with koki-server
via the koki-sdk client and implements lightweight authentication for public access scenarios.

Key features include:

- **Public Listing Views**: Browse and search product/service listings without authentication
- **File Sharing**: Access shared files and documents via public links
- **Reference Data**: Display categories, locations, and amenities for public consumption
- **Tracking Integration**: Track user interactions and page views via RabbitMQ
- **Responsive Design**: Mobile-friendly interfaces optimized for public access
- **Performance Optimization**: Redis caching for frequently accessed content
- **SEO Friendly**: Server-side rendering with semantic HTML for search engine optimization
- **Integration via SDK**: Seamless communication with koki-server REST APIs

## Getting Started

### Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+**
- **Koki Server**: The backend API must be running and accessible
- **RabbitMQ 4.0+**: For tracking event processing
- **Redis 7.0+**: For caching

### Installation

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-portal-public
```

2. Build the project:

```bash
mvn clean install
```

3. Configure the application by creating **application-local.yml** (optional):

```yaml
server:
    port: 8082

koki:
    webapp:
        client-id: koki-portal-public
    server:
        url: http://localhost:8080

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
        storage:
            type: local
            local:
                directory: ${user.home}/__wutsi
```

### Running the Project

Run the application locally:

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/koki-portal-public-VERSION_NUMBER.jar
```

The portal will start on port **8082** by default.

Access the public portal:

```
http://localhost:8082
```

Verify the service is running:

```bash
curl http://localhost:8082/actuator/health
```

Expected response:

```json
{
    "status": "UP"
}
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

