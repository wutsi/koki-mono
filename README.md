# koki-mono

Multi-tenant real estate platform monorepo providing comprehensive property management, listing discovery, lead
tracking, and analytics capabilities with modern cloud-native architecture.

[![master](https://github.com/wutsi/koki-mono/actions/workflows/_master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/_master.yml)
[![pr](https://github.com/wutsi/koki-mono/actions/workflows/_pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/_pr.yml)

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Modules](#modules)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [High-Level System Diagram](#high-level-system-diagram)
- [License](#license)

## Features

- **Multi-Tenancy Architecture**: Complete tenant isolation with context propagation across all services and storage
  layers
- **Core Domain Services**: Comprehensive business logic for listings, offers, leads, accounts, contacts, files,
  messaging, notes, agents, and roles
- **Type-Safe SDK**: Kotlin client library for seamless integration with all platform APIs
- **Administrative Portal**: Full-featured web application for property and account management with server-side
  rendering
- **Public Portal**: SEO-optimized public-facing website for property listing discovery and viewing
- **Event Tracking & Analytics**: Real-time event ingestion, enrichment, and KPI aggregation with device detection, bot
  filtering, and geo-location
- **Infrastructure Abstraction**: Pluggable backends for storage (local/S3), caching (Redis), messaging (RabbitMQ),
  email, translation, and AI integration
- **Security Integration**: JWT-based authentication with role-based access control (RBAC) and Spring Security
- **Database Management**: Flyway migrations for versioned schema management with MySQL support
- **High Test Coverage**: Enforced code coverage thresholds via module-level JaCoCo gates (98% line, 95% class)
- **Structured Logging**: Key-value logging with automatic tenant ID and trace ID propagation
- **Resilience Patterns**: Dead-letter queues, retry logic, and buffered persistence for fault tolerance
- **API Documentation**: Interactive Swagger UI with grouped endpoints for all REST APIs
- **Cloud-Native Design**: Containerization-ready with Heroku deployment support via Procfile
- **Pluggable Storage**: Seamless switching between local filesystem and AWS S3 storage backends

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple?logo=kotlin)
![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Frameworks

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.5.6-green?logo=springsecurity)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.x-green?logo=spring)
![Spring Cache](https://img.shields.io/badge/Spring%20Cache-Module-green?logo=spring)
![Hibernate](https://img.shields.io/badge/Hibernate-6.x-orange?logo=hibernate)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1.3-green?logo=thymeleaf)

### Databases

![MySQL](https://img.shields.io/badge/MySQL-9.5-blue?logo=mysql)
![Flyway](https://img.shields.io/badge/Flyway-10.18-red?logo=flyway)

### Cloud

![AWS S3](https://img.shields.io/badge/AWS%20S3-Storage-orange?logo=amazons3)
![AWS Translate](https://img.shields.io/badge/AWS%20Translate-AI-orange?logo=amazonaws)
![Redis](https://img.shields.io/badge/Redis-7.0+-red?logo=redis)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.2-orange?logo=rabbitmq)

### Tools & Libraries

![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven)
![JUnit](https://img.shields.io/badge/JUnit-6.0.1-green?logo=junit5)
![Mockito](https://img.shields.io/badge/Mockito-Kotlin-yellow)
![JaCoCo](https://img.shields.io/badge/JaCoCo-0.8.14-red)
![ktlint](https://img.shields.io/badge/ktlint-1.7.1-purple)
![Auth0 JWT](https://img.shields.io/badge/Auth0%20JWT-4.5.0-blue)
![Apache Commons](https://img.shields.io/badge/Apache%20Commons-Utilities-red?logo=apache)
![Mustache](https://img.shields.io/badge/Mustache-0.9.14-yellow)
![SpringDoc OpenAPI](https://img.shields.io/badge/SpringDoc-2.8.14-green)

## Modules

| Name                                                 | Status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
|------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [koki-dto](modules/koki-dto)                         | [![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml) [![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml)                                                                                                                                                                                                     |
| [koki-platform](modules/koki-platform)               | [![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml) [![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml) [![coverage](.github/badges/koki-platform-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml)                                           |
| [koki-sdk](modules/koki-sdk)                         | [![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml) [![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml)                                                                                                                                                                                                     |
| [koki-server](modules/koki-server)                   | [![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml) [![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml) [![coverage](.github/badges/koki-server-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml)                                                       |
| [koki-portal](modules/koki-portal)                   | [![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml) [![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml) [![coverage](.github/badges/koki-portal-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml)                                                       |
| [koki-portal-public](modules/koki-portal-public)     | [![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml) [![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml) [![coverage](.github/badges/koki-portal-public-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml)             |
| [koki-tracking-server](modules/koki-tracking-server) | [![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml) [![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml) [![coverage](.github/badges/koki-tracking-server-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml) |

## High-Level Architecture

### Repository Structure

```
koki-mono/
├── .github/                           # GitHub Actions workflows and badges
│   ├── badges/                        # Code coverage badges for modules
│   ├── instructions/                  # Documentation generation instructions
│   ├── prompts/                       # AI prompt templates
│   └── workflows/                     # CI/CD workflow definitions
├── docs/                              # Project documentation
│   ├── design/                        # Design documents and architecture
│   └── workflow/                      # Workflow and process documentation
├── modules/                           # All project modules
│   ├── koki-dto/                      # Shared DTOs for API contracts
│   │   └── src/main/kotlin/com/wutsi/koki/dto/  # Request/response DTOs
│   ├── koki-platform/                 # Shared infrastructure libraries
│   │   └── src/main/kotlin/com/wutsi/koki/platform/  # Storage, cache, messaging
│   ├── koki-sdk/                      # Type-safe Kotlin client library
│   │   └── src/main/kotlin/com/wutsi/koki/sdk/  # API client wrappers
│   ├── koki-server/                   # Core REST API backend
│   │   ├── src/main/kotlin/com/wutsi/koki/  # Business logic and endpoints
│   │   └── src/main/resources/       # Configuration, migrations, templates
│   ├── koki-portal/                   # Administrative web application
│   │   ├── src/main/kotlin/com/wutsi/koki/portal/  # Controllers and services
│   │   └── src/main/resources/templates/  # Thymeleaf templates
│   ├── koki-portal-public/            # Public-facing web application
│   │   ├── src/main/kotlin/com/wutsi/koki/portal/public/  # Public controllers
│   │   └── src/main/resources/templates/  # Public templates
│   └── koki-tracking-server/          # Event tracking and analytics
│       ├── src/main/kotlin/com/wutsi/koki/tracking/  # Event processing
│       └── src/main/resources/       # Configuration and filters
├── pom.xml                            # Parent POM with shared dependencies
├── settings.xml                       # Maven settings for GitHub Packages
├── renovate.json                      # Dependency update configuration
├── CONTRIBUTING.md                    # Contribution guidelines
├── LICENSE.md                         # MIT License
└── README.md                          # This file
```

### High-Level System Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         External Clients                                 │
│  (Mobile Apps, Integrations, Third-party Services)                      │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 │ HTTPS/REST
                                 │
┌────────────────────────────────▼────────────────────────────────────────┐
│                         Web Applications                                 │
│  ┌─────────────────┐  ┌──────────────────┐  ┌──────────────────────┐  │
│  │  koki-portal    │  │ koki-portal-public│ │   koki-sdk          │  │
│  │  (Admin UI)     │  │ (Public Website)  │  │   (Client Library)  │  │
│  └────────┬────────┘  └────────┬──────────┘  └──────────┬───────────┘  │
└───────────┼─────────────────────┼────────────────────────┼──────────────┘
            │                     │                        │
            │ HTTP/REST           │ HTTP/REST              │ HTTP/REST
            │                     │                        │
┌───────────▼─────────────────────▼────────────────────────▼──────────────┐
│                         Core Services Layer                              │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                      koki-server                                  │  │
│  │  (REST API: Accounts, Listings, Offers, Leads, Files, Messages)  │  │
│  └────────────────────┬─────────────────────────────────────────────┘  │
│                       │                                                  │
│  ┌────────────────────▼──────────────────────┐                         │
│  │       koki-tracking-server                 │                         │
│  │  (Event Ingestion & Analytics)             │                         │
│  └────────────────────────────────────────────┘                         │
└────────────┬──────────────────────┬──────────────────────┬──────────────┘
             │                      │                      │
             │                      │                      │
┌────────────▼──────────┐  ┌────────▼────────┐  ┌─────────▼──────────────┐
│  koki-platform        │  │   MySQL         │  │   RabbitMQ             │
│  (Infrastructure)     │  │   Database      │  │   Message Queue        │
│  ┌─────────────────┐  │  │                 │  │                        │
│  │ Storage (S3)    │  │  │  - Accounts     │  │  - Tracking Events     │
│  │ Cache (Redis)   │  │  │  - Listings     │  │  - Email Queue         │
│  │ Email (SMTP)    │  │  │  - Offers       │  │  - Dead Letter Queue   │
│  │ Translation     │  │  │  - Leads        │  │                        │
│  │ AI/LLM          │  │  │  - Files        │  │                        │
│  │ Security/JWT    │  │  │  - Messages     │  │                        │
│  └─────────────────┘  │  │  - Tracking     │  │                        │
└───────────────────────┘  └─────────────────┘  └────────────────────────┘
```

**Key Architectural Patterns:**

- **Multi-Tenant Isolation**: Tenant context is propagated via `X-Tenant-ID` header across all services, ensuring data
  isolation at database, cache, and storage levels.
- **API-First Design**: All functionality exposed via RESTful APIs with OpenAPI documentation.
- **Event-Driven Architecture**: Asynchronous event processing via RabbitMQ for tracking, notifications, and background
  jobs.
- **Modular Monorepo**: Organized as independent Maven modules with clear dependency boundaries (DTO → Platform →
  SDK/Server → Portal).
- **Infrastructure Abstraction**: Pluggable providers for storage, caching, messaging, and AI services enable flexible
  deployment.
- **Data Flow**: Web applications → REST APIs → Business Logic → Database/Cache/Storage → Message Queue → Background
  Processing.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

