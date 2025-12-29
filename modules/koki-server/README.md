# Koki Server

Backend REST API server for the Koki multi-tenant real estate platform providing comprehensive property management,
listing discovery, lead tracking, and analytics capabilities.

[![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml)
[![pull_request](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml)

[![JaCoCo](https://github.com/wutsi/koki-mono/blob/master/.github/badges/koki-server-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml)

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Contributing](#contributing)
    - [Local Development](#local-development)
    - [Testing](#testing)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [High-Level System Diagram](#high-level-system-diagram)
- [API Reference](#api-reference)
- [License](#license)

## Features

- **Multi-Tenant REST API**: Complete tenant isolation with context propagation across all endpoints via X-Tenant-ID
  headers
- **Comprehensive Domain Services**: Property listings with legal information tracking, offers, leads, accounts,
  contacts, files, notes,
  agents, roles, and reference data management
- **Authentication & Authorization**: JWT-based authentication with Spring Security and role-based access control
- **File Management**: Document upload, processing, and storage with support for local filesystem and AWS S3 backends
- **Email Integration**: Template-based email sending with Thymeleaf and attachment support via Spring Mail
- **AI Integration**: Document content extraction and analysis using Apache Tika and AI services
- **Event Publishing**: Domain event publishing to RabbitMQ for event-driven architecture
- **Database Management**: Flyway migrations for versioned schema evolution with MySQL support
- **Caching**: Spring Cache abstraction with Redis support for improved performance
- **API Documentation**: Interactive Swagger UI via SpringDoc OpenAPI for all REST endpoints
- **High Test Coverage**: 92% line and class coverage enforced via JaCoCo thresholds
- **Scheduled Jobs**: Background job execution for data processing and maintenance tasks
- **CSV Import/Export**: Bulk data operations with CSV file support for accounts and attributes
- **PDF Generation**: Dynamic PDF document generation using Flying Saucer and Thymeleaf templates
- **Phone Number Validation**: International phone number parsing and validation using libphonenumber
- **HTML Content Processing**: Web content scraping and sanitization with jsoup

## Technologies

### Programming Languages

[![Kotlin](https://img.shields.io/badge/Kotlin-language-purple?logo=kotlin)](https://kotlinlang.org/)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://www.java.com/)

### Frameworks

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-framework-green?logo=springsecurity)](https://spring.io/projects/spring-security)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-framework-green?logo=spring)](https://spring.io/projects/spring-data-jpa)

### Databases

[![MySQL](https://img.shields.io/badge/MySQL-database-blue?logo=mysql)](https://www.mysql.com/)
[![Flyway](https://img.shields.io/badge/Flyway-migration-red?logo=flyway)](https://flywaydb.org/)

### Cloud

[![AWS S3](https://img.shields.io/badge/AWS%20S3-storage-orange?logo=amazons3)](https://aws.amazon.com/s3/)
[![Heroku](https://img.shields.io/badge/Heroku-deployment-purple?logo=heroku)](https://www.heroku.com/)

### Tools & Libraries

[![Maven](https://img.shields.io/badge/Maven-build-red?logo=apachemaven)](https://maven.apache.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-messaging-orange?logo=rabbitmq)](https://www.rabbitmq.com/)
[![Redis](https://img.shields.io/badge/Redis-caching-red?logo=redis)](https://redis.io/)
[![Apache Tika](https://img.shields.io/badge/Apache%20Tika-text%20extraction-blue?logo=apache)](https://tika.apache.org/)
[![Apache POI](https://img.shields.io/badge/Apache%20POI-office%20docs-blue?logo=apache)](https://poi.apache.org/)
[![PDFBox](https://img.shields.io/badge/PDFBox-pdf%20processing-red?logo=apache)](https://pdfbox.apache.org/)
[![JWT](https://img.shields.io/badge/JWT-authentication-black?logo=jsonwebtokens)](https://jwt.io/)

## Contributing

Contributions are welcome! Please follow the guidelines outlined in our contributing documentation.

For detailed contribution guidelines, please refer to [CONTRIBUTING.md](../../CONTRIBUTING.md).

### Local Development

For instructions on setting up the project for local development, please refer to
the [Local Development Guide](../../CONTRIBUTING.md#getting-the-code).

### Testing

For detailed testing guidelines and best practices, please refer to
the [Testing Section](../../CONTRIBUTING.md#building-the-application) in the contributing guide.

## High-Level Architecture

### Repository Structure

```
koki-server/
├── src/main/kotlin/com/wutsi/koki/
│   ├── Application.kt                    # Spring Boot application entry point
│   ├── account/                          # Account and attribute management
│   │   ├── server/endpoint/              # REST API endpoints
│   │   ├── server/service/               # Business logic layer
│   │   ├── server/dao/                   # Data access objects
│   │   ├── server/domain/                # JPA entities
│   │   └── server/mapper/                # DTO-Entity mappers
│   ├── agent/                            # Agent and user-agent management
│   ├── ai/                               # AI integration services
│   ├── config/                           # Spring configuration classes
│   ├── contact/                          # Contact management
│   ├── email/                            # Email service integration
│   ├── error/                            # Error handling and exception management
│   ├── file/                             # File upload and storage management
│   ├── lead/                             # Lead tracking and management
│   ├── listing/                          # Property listing management
│   ├── module/                           # Module and permission management
│   ├── note/                             # Note taking and management
│   ├── offer/                            # Offer and offer version management
│   ├── refdata/                          # Reference data (categories, locations, amenities)
│   ├── security/                         # Authentication and authorization
│   └── tenant/                           # Tenant and invitation management
├── src/main/resources/
│   ├── application.yml                   # Application configuration
│   ├── db/migration/                     # Flyway database migrations
│   └── templates/                        # Thymeleaf email templates
├── src/test/kotlin/                      # Unit and integration tests
└── pom.xml                               # Maven project configuration
```

### High-Level System Diagram

The Koki Server follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Applications                     │
│         (koki-portal, koki-portal-public, koki-sdk)         │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTPS/REST
┌─────────────────────┴───────────────────────────────────────┐
│                    Presentation Layer                        │
│  - REST Controllers (@RestController)                        │
│  - Request/Response DTOs                                     │
│  - Input Validation (@Valid)                                 │
│  - Error Handling (@RestControllerAdvice)                    │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────┐
│                   Business Logic Layer                       │
│  - Service Classes (@Service)                                │
│  - Domain Business Rules                                     │
│  - Transaction Management (@Transactional)                   │
│  - Event Publishing (RabbitMQ)                               │
│  - Cache Management (@Cacheable)                             │
└─────────┬───────────────────────┬─────────────────┬─────────┘
          │                       │                 │
┌─────────┴──────────┐  ┌────────┴────────┐  ┌────┴─────────┐
│  Data Access Layer │  │ External Services│  │ Infrastructure│
│  - JPA Repositories│  │ - Email Service  │  │ - File Storage│
│  - Entities        │  │ - AI Service     │  │ - Cache (Redis)│
│  - Mappers         │  │                  │  │ - MQ (RabbitMQ)│
└─────────┬──────────┘  └─────────────────┘  └──────────────┘
          │
┌─────────┴──────────┐
│   MySQL Database   │
│  - Tenant Data     │
│  - Versioned Schema│
└────────────────────┘
```

**Data Flow:**

1. **Request Ingress**: Client sends HTTP request with X-Tenant-ID header
2. **Authentication**: Spring Security validates JWT token and extracts user context
3. **Validation**: Controller validates request payload using Bean Validation
4. **Business Logic**: Service layer processes request, applying business rules and tenant isolation
5. **Data Persistence**: JPA repositories interact with MySQL database with tenant filtering
6. **Event Publishing**: Domain events published to RabbitMQ for asynchronous processing
7. **Response**: DTO mapped from domain entities and returned to client

**Key Architectural Patterns:**

- **Layered Architecture**: Clear separation between presentation, business logic, and data access layers
- **Repository Pattern**: Data access abstraction using Spring Data JPA repositories
- **DTO Pattern**: Separation of internal domain models from external API contracts
- **Event-Driven Architecture**: Domain events published via RabbitMQ for loose coupling
- **Multi-Tenancy**: Tenant context propagation and data isolation across all layers
- **Dependency Injection**: Spring IoC container manages all component dependencies

## API Reference

[![Springdoc - LOCAL](https://img.shields.io/badge/Springdoc-LOCAL-blue?logo=swagger)](http://localhost:8080/swagger-ui.html)
[![Springdoc - TEST](https://img.shields.io/badge/Springdoc-TEST-green?logo=swagger)](https://koki-server-test-71da83cfcf1a.herokuapp.com/swagger-ui.html)

### Core API Endpoints

| Method             | Path                              | Description                                     |
|--------------------|-----------------------------------|-------------------------------------------------|
| **Authentication** |
| POST               | `/v1/auth/login`                  | Authenticate user and obtain JWT token          |
| **Accounts**       |
| POST               | `/v1/accounts`                    | Create a new account                            |
| POST               | `/v1/accounts/{id}`               | Update an existing account                      |
| DELETE             | `/v1/accounts/{id}`               | Delete an account                               |
| GET                | `/v1/accounts/{id}`               | Retrieve account details by ID                  |
| GET                | `/v1/accounts`                    | Search accounts with filters                    |
| **Attributes**     |
| GET                | `/v1/attributes/{id}`             | Retrieve attribute details by ID                |
| GET                | `/v1/attributes`                  | Search attributes with filters                  |
| POST               | `/v1/attributes/csv`              | Bulk import attributes from CSV                 |
| GET                | `/v1/attributes/csv`              | Export attributes to CSV                        |
| **Listings**       |
| POST               | `/v1/listings`                    | Create a new property listing                   |
| POST               | `/v1/listings/{id}`               | Update listing details                          |
| POST               | `/v1/listings/{id}/amenities`     | Update listing amenities                        |
| POST               | `/v1/listings/{id}/address`       | Update listing address                          |
| POST               | `/v1/listings/{id}/geo-location`  | Update listing geo-location                     |
| POST               | `/v1/listings/{id}/price`         | Update listing price                            |
| POST               | `/v1/listings/{id}/leasing`       | Update listing leasing terms                    |
| POST               | `/v1/listings/{id}/seller`        | Update listing seller information               |
| POST               | `/v1/listings/{id}/remarks`       | Update listing remarks                          |
| POST               | `/v1/listings/{id}/legal-info`    | Update listing legal information                |
| POST               | `/v1/listings/{id}/publish`       | Publish a listing                               |
| POST               | `/v1/listings/{id}/close`         | Close a listing                                 |
| GET                | `/v1/listings/{id}`               | Retrieve listing details by ID                  |
| GET                | `/v1/listings/{id}/similar`       | Find similar listings based on similarity score |
| GET                | `/v1/listings`                    | Search listings with filters                    |
| **Leads**          |
| POST               | `/v1/leads`                       | Create a new lead                               |
| POST               | `/v1/leads/{id}/status`           | Update lead status                              |
| GET                | `/v1/leads/{id}`                  | Retrieve lead details by ID                     |
| GET                | `/v1/leads`                       | Search leads with filters                       |
| **Contacts**       |
| GET                | `/v1/contacts/{id}`               | Retrieve contact details by ID                  |
| GET                | `/v1/contacts`                    | Search contacts with filters                    |
| **Offers**         |
| GET                | `/v1/offers/{id}`                 | Retrieve offer details by ID                    |
| GET                | `/v1/offers`                      | Search offers with filters                      |
| **Offer Versions** |
| GET                | `/v1/offer-versions/{id}`         | Retrieve offer version details by ID            |
| GET                | `/v1/offer-versions`              | Search offer versions with filters              |
| **Files**          |
| POST               | `/v1/files`                       | Upload a file                                   |
| GET                | `/v1/files/{id}`                  | Retrieve file metadata by ID                    |
| GET                | `/v1/files/{id}/content`          | Download file content                           |
| DELETE             | `/v1/files/{id}`                  | Delete a file                                   |
| **Agents**         |
| GET                | `/v1/agents/{id}`                 | Retrieve agent details by ID                    |
| GET                | `/v1/agents`                      | Search agents with filters                      |
| GET                | `/v1/agents/jobs/metrics/daily`   | Get daily agent job metrics                     |
| GET                | `/v1/agents/jobs/metrics/monthly` | Get monthly agent job metrics                   |
| GET                | `/v1/users/{id}/agent`            | Get agent by user ID                            |
| **Modules**        |
| GET                | `/v1/modules`                     | List all available modules                      |
| **Permissions**    |
| GET                | `/v1/permissions`                 | List all permissions                            |
| **Reference Data** |
| GET                | `/v1/refdata`                     | Get all reference data                          |
| GET                | `/v1/categories`                  | List categories                                 |
| GET                | `/v1/locations`                   | Search locations                                |
| GET                | `/v1/amenities`                   | List amenities                                  |
| **Invitations**    |
| POST               | `/v1/invitations`                 | Create tenant invitation                        |

All endpoints require the `X-Tenant-ID` header for tenant context, except for authentication endpoints.

## License

This project is licensed under the MIT License - see the [LICENSE](../../LICENSE.md) file for details.

