# ARCHITECTURE

## Table of Content

- [Overview](#overview)
- [Project Structure](#project-structure)
- [High-Level System Diagram](#high-level-system-diagram)
- [Core Components](#core-components)
- [Data Stores](#data-stores)
- [External Integrations / APIs](#external-integrations--apis)
- [Deployment & Infrastructure](#deployment--infrastructure)
- [Security Considerations](#security-considerations)

## Overview

The **koki-platform** module is a shared infrastructure library for the Koki ecosystem. It provides reusable,
production-ready abstractions for cross-cutting concerns including storage, messaging, caching, security, templating,
translation, logging, tracing, and AI provider integration. This architecture enables downstream services (**koki-server
**, **koki-portal**, chatbots, etc.) to focus on business logic while maintaining consistency in how they interact with
external systems and handle multi-tenancy.

The architecture follows a **pluggable provider pattern**, allowing services to switch between implementations (e.g.,
local vs S3 storage, different AI providers) through configuration without code changes. This design promotes:

- **Consistency**: All services use the same patterns for infrastructure concerns
- **Maintainability**: Bug fixes and improvements benefit all consumers simultaneously
- **Testability**: Mock implementations simplify unit testing in consuming services
- **Flexibility**: Easy switching between providers (local/cloud, development/production)

Key architectural principles:

- **Separation of concerns**: Each package addresses a single infrastructure domain
- **Configuration-driven behavior**: Runtime behavior controlled via application configuration
- **Fail-fast with clear errors**: Invalid configurations detected early with actionable error messages
- **Observability-first**: Built-in structured logging and tracing context propagation
- **Multi-tenant aware**: Tenant context flows through all major subsystems

## Project Structure

```
modules/koki-platform/
├── pom.xml
├── ARCHITECTURE.md
├── README.md
└── src/
    ├── main/
    │   ├── kotlin/com/wutsi/koki/platform/
    │   │   ├── KokiApplication.kt        (Entry point / auto-configuration)
    │   │   ├── ai/                       (AI provider abstractions & factory)
    │   │   ├── cache/                    (Cache configuration & Redis integration)
    │   │   ├── debug/                    (Debug utilities & feature flags)
    │   │   ├── executor/                 (Async task execution helpers)
    │   │   ├── geoip/                    (Geo-IP lookup services)
    │   │   ├── logger/                   (Structured key-value logging API)
    │   │   ├── messaging/                (Email/notification service interfaces)
    │   │   ├── mq/                       (RabbitMQ low-level integration)
    │   │   ├── security/                 (JWT decoding / security context)
    │   │   ├── storage/                  (Local & S3 storage abstraction)
    │   │   ├── templating/               (Mustache template engine)
    │   │   ├── tenant/                   (Tenant resolution & context)
    │   │   ├── tracing/                  (Tracing instrumentation)
    │   │   ├── tracking/                 (Event tracking / analytics)
    │   │   ├── translation/              (AWS Translate integration)
    │   │   ├── url/                      (URL building & normalization)
    │   │   └── util/                     (Shared utility functions)
    │   └── resources/
    └── test/
        ├── kotlin/
        └── resources/
```

**Structure Principles:**

- **Package-by-capability**: Each top-level package represents a distinct infrastructure concern
- **Interface + Implementation pattern**: Public interfaces in package root, implementations in subpackages
- **Configuration classes**: Each major component has dedicated configuration classes (e.g., `StorageConfiguration`)
- **Factory pattern**: Providers instantiated via factories (e.g., `AIProviderFactory`) based on configuration

## High-Level System Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     Consuming Services                          │
│  (koki-server, koki-portal, koki-chatbot-*, koki-sdk)          │
└─────────────────┬───────────────────────────────────────────────┘
                  │ (compile-time dependency)
                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                      koki-platform                              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Façade Layer                                            │  │
│  │  StorageService | MessagingService | CacheManager       │  │
│  │  TenantProvider | KVLogger | TemplateEngine             │  │
│  └────────────┬──────────────┬────────────┬─────────────────┘  │
│               │              │            │                     │
│  ┌────────────▼──────┐  ┌───▼─────┐  ┌───▼────────┐          │
│  │ Storage Providers │  │   MQ    │  │   Cache    │          │
│  │  - Local          │  │ RabbitMQ│  │   Redis    │          │
│  │  - S3             │  │         │  │            │          │
│  └────────┬──────────┘  └────┬────┘  └─────┬──────┘          │
└───────────┼──────────────────┼─────────────┼──────────────────┘
            │                  │             │
            ▼                  ▼             ▼
     ┌──────────┐      ┌──────────┐   ┌──────────┐
     │  AWS S3  │      │ RabbitMQ │   │  Redis   │
     └──────────┘      └──────────┘   └──────────┘
```

**Data Flow:**

1. **Service invocation**: Consuming service calls high-level API (e.g., `storageService.store()`)
2. **Configuration resolution**: Factory/provider selects implementation based on config (e.g., `storage.type=s3`)
3. **Context enrichment**: Tenant ID, trace ID, user context added automatically
4. **External interaction**: Provider executes operation against external system (S3, RabbitMQ, Redis)
5. **Observability**: Structured logs emitted with full context; tracing spans created
6. **Error handling**: Exceptions wrapped with context; retries applied where configured

**Key Boundaries:**

- **Library boundary**: koki-platform is compile-time only; no runtime process
- **Provider boundary**: External systems accessed only through provider interfaces
- **Tenant boundary**: Tenant isolation enforced at storage paths, cache keys, message headers

## Core Components

### Storage (`storage/`)

**Responsibility**: Unified file/object storage abstraction supporting local filesystem and AWS S3.

**Key Classes:**

- `StorageService`: Main interface for store/retrieve/delete operations
- `LocalStorageService`: Filesystem-based implementation
- `S3StorageService`: AWS S3-based implementation
- `StorageConfiguration`: Configuration properties

**Configuration:**

```yaml
wutsi.platform.storage.type: local | s3
```

### Messaging (`messaging/`, `mq/`)

**Responsibility**: Email/notification composition and RabbitMQ-based async messaging.

**Key Classes:**

- `MessagingService`: High-level interface for sending emails/notifications
- `Message`, `Party`: Email composition models
- `RabbitMQMessagingService`: RabbitMQ implementation with retry and DLQ support

**Configuration:**

```yaml
wutsi.platform.mq.type: rabbitmq
wutsi.platform.mq.rabbitmq.url: amqp://localhost
```

### Cache (`cache/`)

**Responsibility**: Distributed caching via Redis with Spring Cache abstraction.

**Key Classes:**

- `CacheConfiguration`: Spring Cache configuration
- Redis client configuration using Lettuce

**Configuration:**

```yaml
wutsi.platform.cache.type: redis
wutsi.platform.cache.redis.url: redis://localhost:6379
```

### AI Provider (`ai/`)

**Responsibility**: Pluggable AI/LLM provider abstraction for generative tasks.

**Key Classes:**

- `AIProvider`: Interface for LLM interactions
- `AIProviderFactory`: Creates provider instances from configuration
- `AIProviderConfiguration`: Provider-specific configuration (API keys, models, etc.)

### Security (`security/`)

**Responsibility**: JWT token decoding and security context management.

**Key Classes:**

- `JWTDecoder`: Token parsing and validation
- `AccessTokenHolder`: Thread-safe token storage for request context

### Tenant (`tenant/`)

**Responsibility**: Multi-tenant context resolution and propagation.

**Key Classes:**

- `TenantProvider`: Resolves tenant ID from request context (headers, JWT, subdomain)
- Tenant context flows to storage paths, cache keys, logging

### Templating (`templating/`)

**Responsibility**: Dynamic template rendering using Mustache.

**Key Classes:**

- `TemplateEngine`: Interface for template rendering
- Mustache compiler wrapper for variable substitution

### Logging (`logger/`)

**Responsibility**: Structured key-value logging for machine-parsable output.

**Key Classes:**

- `KVLogger`: Structured logger with automatic tenant/trace context
- Thread-local context for correlation IDs

### Translation (`translation/`)

**Responsibility**: Multilingual content translation via AWS Translate.

**Key Classes:**

- AWS Translate SDK integration
- Language code normalization utilities

### Tracking (`tracking/`)

**Responsibility**: Analytics event publishing for audit and observability.

**Key Classes:**

- Event publishing helpers leveraging messaging/MQ subsystems

### GeoIP (`geoip/`)

**Responsibility**: IP address geolocation for localization and security.

### Executor (`executor/`)

**Responsibility**: Async task execution and thread pool management.

### Debug (`debug/`)

**Responsibility**: Feature flags and debug mode controls.

### Utilities (`util/`)

**Responsibility**: Shared helper functions for string manipulation, collections, etc.

## Data Stores

**koki-platform does not own persistent data stores.** It provides integration with external stores used by consuming
services:

### Redis (Cache)

- **Purpose**: Ephemeral performance optimization
- **Data**: Cached query results, session data
- **Scope**: Distributed across service instances
- **TTL**: Configurable per cache region

### S3 (Object Storage)

- **Purpose**: Binary content storage (files, documents, media)
- **Data**: User-uploaded files, generated reports, email attachments
- **Organization**: Tenant-prefixed paths for isolation

### RabbitMQ (Message Broker)

- **Purpose**: Asynchronous event distribution
- **Data**: Transient messages (events, notifications, commands)
- **Pattern**: Pub/sub via exchanges, with retry queues and DLQs

**Note**: Relational database schemas are owned by consuming services (e.g., **koki-server**). This module provides
Hibernate utility types (JSON mapping) but no entity definitions.

## External Integrations / APIs

### AWS Services

**AWS S3**

- **Purpose**: Object storage for production deployments
- **Authentication**: IAM credentials or instance profile
- **SDK**: `aws-java-sdk-s3`

**AWS Translate**

- **Purpose**: Machine translation for multilingual support
- **Authentication**: IAM credentials
- **SDK**: `aws-java-sdk-translate`

### RabbitMQ

- **Purpose**: Asynchronous messaging and event distribution
- **Protocol**: AMQP
- **Features**: Retry logic, dead-letter queues, message durability
- **Client**: `amqp-client`

### Redis

- **Purpose**: Distributed caching and session storage
- **Protocol**: Redis protocol
- **Client**: Lettuce (`lettuce-core`)

### AI/LLM Providers

- **Supported**: OpenAI, Anthropic (extensible)
- **Purpose**: Generative AI tasks (text completion, summarization)
- **Configuration**: API keys, model selection, temperature

### Spring Framework

- **Scope**: Provided (compile-time only)
- **Components**: `spring-web`, `spring-boot-autoconfigure`, `spring-security-core`, `spring-boot-actuator`
- **Purpose**: Autoconfiguration and integration hooks

### Jakarta APIs

- **Scope**: Provided
- **APIs**: `jakarta.mail-api`, `jakarta.servlet-api`
- **Purpose**: Email composition and servlet context access

### Hibernate

- **Scope**: Provided
- **Purpose**: Extended types for JSON/array mapping in consumer ORMs

## Deployment & Infrastructure

### Build & Packaging

**Build Tool**: Maven

**Lifecycle**:

```bash
mvn clean install
```

**Artifact**: `com.wutsi.koki:koki-platform:VERSION_NUMBER` (JAR)

**Publishing**: GitHub Packages (`https://maven.pkg.github.com/wutsi/koki-mono`)

### CI/CD

**Pull Request Workflow** (`koki-platform-pr.yml`):

- Compile Kotlin sources
- Run unit tests
- Code coverage analysis (Jacoco thresholds: line ≥ 0.85, class ≥ 0.85)
- Static analysis and linting

**Master Workflow** (`koki-platform-master.yml`):

- Build and test
- Publish artifact to GitHub Packages
- Update coverage badge

### Configuration

Consuming services configure via **application.yml**:

```yaml
wutsi:
    platform:
        storage:
            type: local  # or s3
            local:
                directory: /var/koki/storage
                base-url: http://localhost:8080
            s3:
                bucket: [ REPLACE_WITH_BUCKET_NAME ]
                region: [ REPLACE_WITH_AWS_REGION ]
        mq:
            type: rabbitmq
            rabbitmq:
                url: amqp://[REPLACE_WITH_HOST]
                exchange-name: koki-events
                max-retries: 3
                ttl-seconds: 86400
        cache:
            type: redis
            ttl: 3600
            redis:
                url: redis://[REPLACE_WITH_HOST]:6379
```

### Runtime Requirements

**For consuming services:**

- Java 17+
- Spring Boot 3.5.7+
- External service endpoints (RabbitMQ, Redis, AWS) as configured
- GitHub Packages credentials for artifact resolution

**No runtime process for koki-platform itself** - it's a library.

### Observability

- **Structured Logging**: KVLogger outputs key-value pairs for log aggregation
- **Tracing**: Correlation IDs propagated automatically
- **Metrics**: Consumers expose actuator endpoints; platform utilities enrich health checks

## Security Considerations

### Authentication & Authorization

- **JWT Handling**: `JWTDecoder` validates tokens; consumers must configure proper algorithms (HS256/RS256)
- **Token Claims**: Custom claims include tenant ID and user ID for multi-tenant isolation
- **Access Control**: Security context available via `AccessTokenHolder`; authorization logic in consumers

### Multi-Tenancy

- **Tenant Resolution**: `TenantProvider` extracts tenant ID from JWT claims, headers, or subdomains
- **Isolation**: Tenant ID automatically incorporated into:
    - Storage paths (S3 keys, local directories)
    - Cache keys (Redis namespacing)
    - Message headers (RabbitMQ routing)
    - Structured logs (context field)

### Data Protection

- **Encryption in Transit**: HTTPS/TLS enforced at consumer service boundaries
- **Encryption at Rest**: S3 server-side encryption configured externally
- **PII Handling**: Avoid logging sensitive fields; structured logging uses keys, not full payloads

### Secrets Management

- **Configuration**: Sensitive values (AWS keys, Redis passwords, RabbitMQ credentials) injected via:
    - Environment variables
    - Spring Cloud Config
    - Kubernetes secrets
- **No hardcoded secrets**: All examples use placeholders

### Dependency Security

- **Minimal Surface**: Provided-scope dependencies reduce bundled transitive deps
- **Automated Scanning**: Renovate/Dependabot for vulnerability alerts
- **Version Management**: Centralized in parent POM

### Input Validation

- **File Uploads**: Storage service should validate:
    - File size limits
    - Content type restrictions
    - Path traversal prevention
- **Template Rendering**: Mustache escaping enabled by default
- **Message Payloads**: Size limits enforced at RabbitMQ configuration

### Production Hardening

**For consuming services:**

1. Replace default JWT algorithms with HMAC256 or RSA256 with strong keys
2. Enable S3 bucket encryption and versioning
3. Use Redis AUTH and TLS in production
4. Configure RabbitMQ user permissions (least privilege)
5. Implement rate limiting at API gateway (outside platform scope)
6. Redact PII from logs using structured logging filters
7. Monitor and alert on security events (failed auth, unusual patterns)

