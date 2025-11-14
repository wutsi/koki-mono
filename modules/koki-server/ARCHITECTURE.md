# ARCHITECTURE

## Table of Content

- [Overview](#overview)
- [Project Structure](#project-structure)
- [High-Level System Diagram](#high-level-system-diagram)
- [Core Components](#core-components)
    - [Application Bootstrap](#application-bootstrap)
    - [Security Layer](#security-layer)
    - [API Controllers](#api-controllers)
    - [Service Layer](#service-layer)
    - [Data Access Layer](#data-access-layer)
    - [Domain Entities](#domain-entities)
    - [Configuration](#configuration)
- [Data Stores](#data-stores)
- [Deployment & Infrastructure](#deployment--infrastructure)
- [Security Considerations](#security-considerations)

## Overview

This document describes the architecture of the **koki-server** module. The service is the core backend of the Koki
platform, providing multi-tenant REST APIs for account management, contacts, listings, leads, messaging, files,
payments, and configuration. It implements a layered architecture with Spring Boot, utilizing JWT-based authentication,
JPA for persistence, and various integration libraries for document processing, email delivery, and external service
communication.

## Project Structure

```
modules/koki-server/
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/wutsi/koki/
    │   │       ├── Application.kt
    │   │       ├── account/server/
    │   │       │   ├── endpoint/
    │   │       │   ├── service/
    │   │       │   ├── dao/
    │   │       │   ├── domain/
    │   │       │   ├── mapper/
    │   │       │   └── io/
    │   │       ├── agent/server/
    │   │       ├── ai/server/
    │   │       ├── config/
    │   │       ├── contact/server/
    │   │       ├── email/server/
    │   │       ├── error/server/
    │   │       ├── file/server/
    │   │       ├── lead/server/
    │   │       ├── listing/server/
    │   │       ├── message/server/
    │   │       ├── module/server/
    │   │       ├── note/server/
    │   │       ├── offer/server/
    │   │       ├── refdata/server/
    │   │       ├── security/server/
    │   │       ├── tenant/server/
    │   │       └── translation/server/
    │   └── resources/
    │       ├── application.yml
    │       ├── db/migration/
    │       ├── email/
    │       └── messages.properties
    └── test/
```

## High-Level System Diagram

```
External Clients (Web, Mobile, Portal)
        |
        v
  API Gateway / Load Balancer
        |
        v
  Spring Security Filter Chain
        |  JWT Authentication
        |  Tenant Context
        v
  REST Controllers (Endpoints)
        |
        v
  Service Layer (Business Logic)
        |
        v
  Data Access Layer (JPA Repositories)
        |
        v
  MySQL Database

External Integrations:
  - RabbitMQ (Message Queue)
  - Redis (Cache)
  - S3/Local Storage (Files)
  - Stripe/PayPal (Payments)
  - Email Server (SMTP)
  - AI Services (LLM)
```

## Core Components

### Application Bootstrap

**Purpose:** Entry point for the Spring Boot application with essential configuration annotations.
**Key Functions:** Enables JPA repositories, entity scanning across all domain packages, transaction management,
caching, async processing, and scheduling.
**Interactions:** Scans and initializes all Spring beans, establishes database connections, and configures the
application context.

### Security Layer

**Purpose:** Implements authentication and authorization for all API endpoints.
**Key Functions:** JWT token validation, tenant context extraction from headers, role-based access control, and request
filtering.
**Interactions:** Intercepts all incoming HTTP requests, validates credentials, establishes security context, and
enforces access policies.

### API Controllers

**Purpose:** Expose RESTful HTTP endpoints organized by business domain.
**Components & Functions:**

- **Account Endpoints**: User account CRUD operations, profile management
- **Contact Endpoints**: Contact management and relationship tracking
- **Listing Endpoints**: Product/service listing management
- **Lead Endpoints**: Lead capture and pipeline management
- **Message Endpoints**: Internal messaging and notifications
- **File Endpoints**: File upload, storage, and retrieval
- **Offer Endpoints**: Offer creation and versioning
- **Tenant Endpoints**: Multi-tenant configuration and management
- **Module Endpoints**: Feature module configuration and permissions
- **RefData Endpoints**: Reference data (categories, locations, amenities)
  **Interactions:** Receive HTTP requests, validate input, delegate to service layer, and return HTTP responses with
  appropriate status codes.

### Service Layer

**Purpose:** Encapsulates business logic and orchestrates operations across multiple domain entities.
**Key Functions:** Transaction management, business rule validation, data transformation, integration with external
services, and event publishing.
**Interactions:** Called by controllers, invokes DAOs for persistence, coordinates cross-cutting concerns (logging,
caching, messaging).

### Data Access Layer

**Purpose:** Provides abstraction for database operations using Spring Data JPA repositories.
**Key Functions:** CRUD operations, custom query methods, transaction management, and entity state management.
**Interactions:** Used by service layer to persist and retrieve domain entities from MySQL database.

### Domain Entities

**Purpose:** Represent the core business objects mapped to database tables.
**Key Models:** AccountEntity, ContactEntity, ListingEntity, LeadEntity, MessageEntity, FileEntity, OfferEntity,
TenantEntity, ModuleEntity, and various configuration entities.
**Interactions:** Managed by JPA, persisted via repositories, transformed by mappers for API responses.

### Configuration

**Purpose:** Centralizes application configuration and bean definitions.
**Key Functions:** Database connection pooling (HikariCP), Flyway migration setup, OpenAPI documentation, security
policies, cache configuration, and message queue setup.
**Interactions:** Provides configuration beans consumed by other components.

## Data Stores

- **MySQL Database**: Primary relational database for persistent storage of all business entities. Uses HikariCP for
  connection pooling and Flyway for schema versioning.
- **Redis Cache**: Distributed cache for session management, frequently accessed data, and temporary storage.
- **RabbitMQ**: Message broker for asynchronous event processing and inter-service communication.
- **File Storage (S3 or Local)**: Object storage for uploaded files, documents, and generated reports.

## Deployment & Infrastructure

Build:

```bash
mvn clean install
```

Artifact: Executable JAR `koki-server-VERSION_NUMBER.jar`.

Runtime Profiles:

- **local**: Local development with embedded configurations.
- **test**: Testing environment with test database.
- **prod**: Production environment with external services.

Key Configuration:

```yaml
spring.datasource.url: jdbc:mysql://[HOST]:3306/koki
spring.datasource.username: [ DB_USER ]
spring.datasource.password: [ DB_PASSWORD ]
wutsi.platform.mq.rabbitmq.url: amqp://[RABBIT_HOST]
wutsi.platform.cache.redis.url: redis://[REDIS_HOST]:6379
wutsi.platform.storage.type: local | s3
```

Database Migrations:

- Flyway automatically applies migrations from `src/main/resources/db/migration/` on startup.
- Migrations organized by environment (common, local, test, prod).

Scaling:

- Stateless service design allows horizontal scaling.
- Database connection pooling optimized for concurrent requests.
- Redis cache reduces database load.
- Async processing for non-blocking operations.

Observability:

- Actuator endpoints for health, metrics, and application info.
- Structured logging with contextual information (tenant, user, request).
- OpenAPI documentation at `/api.html`.

## Security Considerations

- **Authentication**: JWT token-based authentication using Auth0 library. Tokens must be included in Authorization
  header.
- **Authorization**: Role-based access control with permissions managed per tenant and module.
- **Multi-Tenancy**: Tenant ID required in `X-Tenant-ID` header for all authenticated requests. Data isolation enforced
  at service layer.
- **Input Validation**: Bean validation annotations on DTOs, custom validators for business rules.
- **SQL Injection Prevention**: Parameterized queries via JPA/Hibernate.
- **Transport Security**: HTTPS recommended for production deployments.
- **Secrets Management**: Database credentials, API keys, and tokens externalized via environment variables or
  configuration server.
- **CORS**: Configurable cross-origin policies for web client access.
- **Rate Limiting**: Application-level rate limiting can be implemented via interceptors.
- **Audit Logging**: User actions logged with tenant and user context for compliance.

