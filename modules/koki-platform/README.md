# koki-platform

A shared infrastructure library providing reusable abstractions for cross-cutting concerns across the Koki platform.

![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml/badge.svg)

![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml/badge.svg)

![JaCoCo](../../.github/badges/koki-platform-jacoco.svg)

![Java](https://img.shields.io/badge/Java-17-blue)

![Kotlin](https://img.shields.io/badge/Kotlin-language-purple)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)

## Table of Contents

- [About the Project](#about-the-project)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Project](#running-the-project)
    - [Running Tests](#running-tests)
- [License](#license)

## About the Project

The **koki-platform** is a shared infrastructure library that provides reusable, production-ready abstractions for
cross-cutting concerns across all Koki services. It enables downstream applications (koki-server, koki-portal,
koki-tracking-server, chatbots, etc.) to focus on business logic while maintaining consistency in how they interact with
external systems and handle infrastructure concerns.

Key features include:

- **Storage Abstraction**: Unified interface for local filesystem and AWS S3 storage with pluggable providers
- **Messaging Infrastructure**: RabbitMQ integration with retry logic, dead-letter queues, and message durability
- **Distributed Caching**: Redis-backed caching with Spring Cache abstraction
- **Multi-Tenancy Support**: Tenant context resolution and propagation through storage paths, cache keys, and message
  headers
- **Security Integration**: JWT token decoding and security context management
- **Structured Logging**: Key-value logging with automatic tenant and trace context propagation
- **Template Engine**: Mustache-based dynamic template rendering
- **Translation Services**: AWS Translate integration for multilingual support
- **AI Provider Abstraction**: Pluggable AI/LLM provider interfaces for generative tasks
- **GeoIP Services**: IP address geolocation capabilities
- **Async Execution**: Thread pool management and async task execution helpers

This architecture promotes consistency, maintainability, testability, and flexibility by allowing services to switch
between implementations (local vs. cloud, development vs. production) through configuration without code changes.

## Getting Started

### Prerequisites

This is a library module consumed by other Koki services. To work with it directly:

- **Java 17** or higher
- **Maven 3.8+**
- **Spring Boot 3.5.7+** (for consuming services)

### Installation

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-platform
```

2. Build the library:

```bash
mvn clean install
```

3. To use in a consuming service, add the dependency:

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-platform</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

### Running the Project

This is a library module and does not run independently. It is consumed as a dependency by other Koki services.

To verify the build:

```bash
mvn clean verify
```

### Running Tests

Execute unit tests:

```bash
mvn test
```

Run all tests with coverage:

```bash
mvn clean test jacoco:report
```

The coverage report will be available at **target/site/jacoco/index.html**.

Coverage thresholds:

- Line coverage: ≥ 85%
- Class coverage: ≥ 85%

## License

This project is licensed under the MIT License. See [LICENSE.md](../../LICENSE.md) for details.
