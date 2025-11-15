# koki-mono

A Spring Boot monorepo delivering a multi-tenant real estate platform with REST APIs, SDK, portals, and tracking
services.

![master](https://github.com/wutsi/koki-mono/actions/workflows/_master.yml/badge.svg)

![pr](https://github.com/wutsi/koki-mono/actions/workflows/_pr.yml/badge.svg)

[REPLACE WITH COVERAGE BADGE]

![Java](https://img.shields.io/badge/Java-17-blue)

![Kotlin](https://img.shields.io/badge/Kotlin-language-purple)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)

![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)

![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0-orange)

![Redis](https://img.shields.io/badge/Redis-7.0-red)

## Table of Contents

- [About the Project](#about-the-project)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Project](#running-the-project)
    - [Running Tests](#running-tests)
- [Modules](#modules)
- [API References](#api-references)
- [License](#license)

## About the Project

**koki-mono** is a comprehensive monorepo for the Koki real estate platform. It provides backend APIs, client SDKs,
operational portals, and tracking services to support property listing management, lead generation, tenant operations,
messaging, payments, and analytics. Built with Spring Boot 3.5.7 and Kotlin, the platform emphasizes multi-tenancy,
security, and extensibility.

Key value propositions:

- **Multi-Module Architecture**: Separation of concerns across DTOs, infrastructure (platform), business services (
  server), presentation portals, tracking, and SDK.
- **Consistency & Reuse**: Shared koki-platform module unifies storage, messaging, caching, logging, and multi-tenancy
  primitives.
- **Scalability**: Modular services can scale independently (API, portals, tracking server).
- **Extensibility**: Pluggable providers for storage (local/S3), messaging (RabbitMQ), cache (Redis), AI, and
  translation.
- **Security & Isolation**: JWT-based auth with tenant scoping via headers and propagated context.

Problems solved:

- Reduces duplicated boilerplate across services.
- Accelerates onboarding and new feature development through shared abstractions.
- Standardizes cross-cutting concerns (observability, multi-tenancy, messaging).
- Provides end-to-end platform from ingestion to user experience to analytics.

## Getting Started

### Prerequisites

Install and configure the following locally:

- **Java 17** (JDK)
- **Maven 3.8+**
- **MySQL 8.0+** (local instance; root user preferably passwordless for dev)
- **RabbitMQ 4.0+** (for messaging & tracking modules)
- **Redis 7.0+** (for caching used by some modules)
- **Git**

Optional (used by some features):

- **AWS Credentials** (S3, Translate)
- **Stripe / PayPal API keys** (payments)

### Installation

Clone and build the entire monorepo:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono
mvn clean install
```

This command builds all modules and runs their tests.

### Running the Project

The root project is an aggregator POM and does not run directly. Run individual service modules:

Run backend server (core APIs):

```bash
cd modules/koki-server
mvn spring-boot:run
```

Server default port: `8080`.

Run administrative portal:

```bash
cd modules/koki-portal
mvn spring-boot:run
```

Portal default port: `8081`.

Run public portal:

```bash
cd modules/koki-portal-public
mvn spring-boot:run
```

Public portal default port: `8082`.

Run tracking server:

```bash
cd modules/koki-tracking-server
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Tracking server default port: `8083`.

### Running Tests

Run tests for all modules from root:

```bash
mvn test
```

Run full verification with coverage:

```bash
mvn verify
```

Generate module-specific coverage (example for `koki-server`):

```bash
cd modules/koki-server
mvn clean test jacoco:report
```

Coverage reports: `modules/<module>/target/site/jacoco/index.html`.

## Modules

| Name                                                 | Status                                                                                                                                                                                                                                                                  |
|------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [koki-dto](modules/koki-dto)                         | ![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg) ![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)                                                                                     |
| [koki-platform](modules/koki-platform)               | ![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml/badge.svg) ![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml/badge.svg) ![Coverage](.github/badges/koki-platform-jacoco.svg)                      |
| [koki-sdk](modules/koki-sdk)                         | ![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml/badge.svg) ![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml/badge.svg)                                                                                     |
| [koki-server](modules/koki-server)                   | ![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml/badge.svg) ![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml/badge.svg) ![Coverage](.github/badges/koki-server-jacoco.svg)                            |
| [koki-portal](modules/koki-portal)                   | ![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml/badge.svg) ![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml/badge.svg) ![Coverage](.github/badges/koki-portal-jacoco.svg)                            |
| [koki-portal-public](modules/koki-portal-public)     | ![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml/badge.svg) ![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml/badge.svg) ![Coverage](.github/badges/koki-portal-public-jacoco.svg)       |
| [koki-tracking-server](modules/koki-tracking-server) | ![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml/badge.svg) ![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml/badge.svg) ![Coverage](.github/badges/koki-tracking-server-jacoco.svg) |

(Additional chatbot modules are commented out in the root pom.)

## API References

Core backend server (koki-server) publishes REST APIs with OpenAPI documentation.

Swagger UI:

```
http://localhost:8080/api.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

Example: Fetch an account (authenticated)

```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "X-Tenant-ID: 1" \
     http://localhost:8080/v1/accounts/1
```

Tracking server health:

```bash
curl http://localhost:8083/actuator/health
```

Portal access:

- Admin: `http://localhost:8081`
- Public: `http://localhost:8082`

## License

This project is licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for details.
