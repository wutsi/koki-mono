# Koki Platform Architecture

## Table of Contents
- [Overview](#overview)
- [Module Architecture](#module-architecture)
- [Domain Structure](#domain-structure)
- [Security & Multi-Tenancy](#security--multi-tenancy)
- [Infrastructure Abstractions](#infrastructure-abstractions)
- [Event-Driven Architecture](#event-driven-architecture)
- [Data Flow Examples](#data-flow-examples)

## Overview

Koki is a **multi-tenant real estate platform** built as a **modular monorepo** using Kotlin, Spring Boot, and cloud-native technologies. The architecture emphasizes clean separation of concerns, infrastructure flexibility, and tenant isolation.

### High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      External Clients                            │
│                  (Mobile Apps, Third-party)                      │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTPS/REST
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                       Application Layer                          │
│  ┌────────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │  koki-portal   │  │ koki-portal  │  │   koki-sdk       │   │
│  │  (Admin UI)    │  │ -public      │  │   (Client Lib)   │   │
│  │  Spring MVC    │  │ (Public Web) │  │   RestTemplate   │   │
│  │  Thymeleaf     │  │ Thymeleaf    │  │   Type-safe API  │   │
│  └───────┬────────┘  └──────┬───────┘  └────────┬─────────┘   │
└──────────┼────────────────────┼──────────────────┼─────────────┘
           │                    │                  │
           │ HTTP/REST          │                  │
           └────────────────────┴──────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                      Business Logic Layer                        │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    koki-server                            │  │
│  │  ┌──────────┬───────────┬────────────┬─────────────┐    │  │
│  │  │ Listing  │  Account  │  Contact   │   Lead      │    │  │
│  │  │ Domain   │  Domain   │  Domain    │   Domain    │    │  │
│  │  └──────────┴───────────┴────────────┴─────────────┘    │  │
│  │  ┌──────────┬───────────┬────────────┬─────────────┐    │  │
│  │  │  Agent   │   File    │   Tenant   │   Security  │    │  │
│  │  │  Domain  │   Domain  │   Domain   │   Domain    │    │  │
│  │  └──────────┴───────────┴────────────┴─────────────┘    │  │
│  │                                                            │  │
│  │  Endpoint → Service → Repository → Entity                │  │
│  └────────────────────┬───────────────────────────────────────┘│
│                       │                                         │
│  ┌────────────────────▼──────────────┐                         │
│  │    koki-tracking-server            │                         │
│  │    (Event Analytics)               │                         │
│  └────────────────────────────────────┘                         │
└────────────────┬───────────────┬──────────────┬─────────────────┘
                 │               │              │
┌────────────────▼──────┐  ┌─────▼─────┐  ┌────▼────────────────┐
│  koki-platform        │  │  MySQL    │  │   RabbitMQ          │
│  (Infrastructure)     │  │  Database │  │   Message Queue     │
│  ┌─────────────────┐  │  │           │  │                     │
│  │ Storage (S3)    │  │  │ - Tenants │  │ - Events            │
│  │ Cache (Redis)   │  │  │ - Listings│  │ - Notifications     │
│  │ Email (SMTP)    │  │  │ - Leads   │  │ - DLQ + Retry       │
│  │ AI/LLM          │  │  │ - Files   │  │                     │
│  │ Translation     │  │  │ - etc.    │  │                     │
│  │ Security/JWT    │  │  │           │  │                     │
│  └─────────────────┘  │  │           │  │                     │
└───────────────────────┘  └───────────┘  └─────────────────────┘
```

## Module Architecture

### Module Dependency Graph

```
┌─────────────────┐
│   koki-dto      │  ← Shared DTOs (Request/Response objects)
│   (Contracts)   │     - No dependencies
└────────┬────────┘
         │
         │ depends on
         ↓
┌─────────────────┐
│ koki-platform   │  ← Infrastructure abstractions
│ (Infrastructure)│     - Storage, Cache, Messaging, Security
└────────┬────────┘     - AI/LLM, Translation, Email
         │
         │ depends on
         ↓
    ┌────┴────┐
    │         │
┌───▼──────┐  │  ┌─────────────┐
│koki-sdk  │  └─▶│koki-server  │  ← Core business logic
│(Client)  │     │(REST API)   │     - Domain services
└────┬─────┘     └──────┬──────┘     - JPA entities
     │                  │             - REST endpoints
     │                  │
     │ used by          │ used by
     ↓                  ↓
┌──────────────┐   ┌───────────────┐   ┌─────────────────┐
│ koki-portal  │   │ koki-portal   │   │koki-tracking    │
│ (Admin UI)   │   │ -public       │   │-server          │
└──────────────┘   │ (Public Web)  │   │(Analytics)      │
                   └───────────────┘   └─────────────────┘
```

**Key Principle**: Dependencies flow downward. Lower layers never depend on higher layers.

## Domain Structure

### Business Domains in koki-server

Each domain follows **Clean Architecture** with consistent layering:

```
domain-name/
└── server/
    ├── endpoint/           # REST controllers
    │   └── *Endpoints.kt   # @RestController with multiple endpoints
    ├── service/            # Business logic
    │   ├── *Service.kt     # Core domain service
    │   └── *Validator.kt   # Business rule validation
    ├── dao/                # Data access
    │   └── *Repository.kt  # Spring Data JPA
    ├── domain/             # Data model
    │   └── *Entity.kt      # JPA entities
    ├── mapper/             # DTO conversion
    │   └── *Mapper.kt      # Entity ↔ DTO mapping
    ├── config/             # Configuration
    │   └── *Configuration.kt
    ├── io/                 # Import/Export (if needed)
    │   ├── *CSVImporter.kt
    │   └── *CSVExporter.kt
    └── job/                # Scheduled tasks (if needed)
        └── *Job.kt
```

### Complete Domain List

| Domain | Purpose | Key Entities |
|--------|---------|--------------|
| **listing** | Property listing management | Listing, ListingStatus |
| **account** | User account management | Account, Attribute |
| **contact** | Contact/CRM | Contact |
| **lead** | Lead tracking | Lead, LeadMessage |
| **agent** | Real estate agents | Agent |
| **file** | File/document storage | File |
| **tenant** | Multi-tenant admin | Tenant, User, Role, Configuration |
| **module** | Feature modules | Module, Permission |
| **place** | Location data | Place (neighborhoods, POIs) |
| **refdata** | Reference data | Category, Amenity, Location |
| **email** | Email notifications | Email queue processing |
| **security** | Authentication | JWT, Login |
| **webscraping** | Web content extraction | Website, Webpage |
| **error** | Error handling | Centralized exceptions |

## Security & Multi-Tenancy

### JWT Authentication Flow

```
┌──────────────┐
│ HTTP Request │
│ with JWT     │
└──────┬───────┘
       │
       ↓
┌─────────────────────────────┐
│ JWTAuthenticationFilter     │  (koki-platform)
│  ├─ Extract token from:     │
│  │  - Authorization header  │
│  │  - Cookie                │
│  ├─ Decode JWT              │
│  └─ Set SecurityContext     │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ JWTPrincipal (Claims)       │
│  ├─ userId: Long            │
│  ├─ tenantId: Long          │
│  ├─ application: String     │
│  ├─ subjectType: String     │
│  └─ subject: String         │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ Controller Endpoint         │
│  @RequestHeader             │
│  X-Tenant-ID: tenantId      │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ Service Layer               │
│  - All queries filtered by  │
│    tenantId                 │
│  - Data isolation enforced  │
└─────────────────────────────┘
```

### Multi-Tenant Isolation

**Data Layer:**
```kotlin
// Every entity has tenantId
@Entity
class ListingEntity(
    @Column(nullable = false)
    var tenantId: Long = 0,
    // ... other fields
)

// Queries automatically filtered
@Query("SELECT l FROM ListingEntity l WHERE l.tenantId = :tenantId")
fun findByTenantId(tenantId: Long): List<ListingEntity>
```

**Storage Layer:**
```
Tenant-aware S3 paths:
s3://bucket/tenants/{tenantId}/listings/{listingId}/files/
```

**Cache Layer:**
```
Tenant-scoped cache keys:
listing:tenant:{tenantId}:listing:{listingId}
```

**Request Context:**
```kotlin
// ThreadLocal holder
TenantHolder.set(tenantId)
val currentTenant = TenantHolder.get()
```

## Infrastructure Abstractions

### koki-platform Providers

The platform module provides **pluggable infrastructure**:

```
┌─────────────────────────────────────────────────────────┐
│                   koki-platform                          │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Storage Abstraction        Cache Abstraction           │
│  ┌──────────────────┐      ┌──────────────────┐        │
│  │ StorageService   │      │ CacheService     │        │
│  ├──────────────────┤      ├──────────────────┤        │
│  │ - Local          │      │ - Redis          │        │
│  │ - S3             │      │ - Local          │        │
│  └──────────────────┘      │ - NoCache        │        │
│                            └──────────────────┘        │
│                                                          │
│  Messaging Queue            Email Service               │
│  ┌──────────────────┐      ┌──────────────────┐        │
│  │ RabbitMQ         │      │ SMTP             │        │
│  │ - Publisher      │      │ - Templates      │        │
│  │ - Consumer       │      │ - Attachments    │        │
│  │ - DLQ/Retry      │      └──────────────────┘        │
│  └──────────────────┘                                   │
│                                                          │
│  AI/LLM Services            Translation                 │
│  ┌──────────────────┐      ┌──────────────────┐        │
│  │ - Gemini         │      │ AWS Translate    │        │
│  │ - Deepseek       │      └──────────────────┘        │
│  │ - Kimi           │                                   │
│  └──────────────────┘      Security/JWT                 │
│                            ┌──────────────────┐        │
│  Structured Logging        │ JWT Decoder      │        │
│  ┌──────────────────┐      │ Token Holders    │        │
│  │ KVLogger         │      └──────────────────┘        │
│  │ - MDC Context    │                                   │
│  └──────────────────┘                                   │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Storage Service Example

```kotlin
interface StorageService {
    fun store(path: String, content: InputStream, contentType: String?)
    fun get(path: String, visitor: (InputStream) -> Unit)
    fun delete(path: String)
    fun contains(path: String): Boolean
}

// Configured via application.yml:
storage:
  type: s3  # or "local"
  s3:
    bucket: my-bucket
  local:
    directory: /tmp/storage
```

## Event-Driven Architecture

### RabbitMQ Message Flow

```
┌─────────────────┐
│ Domain Service  │
│ publishes event │
└────────┬────────┘
         │
         ↓
┌────────────────────────────┐
│ Publisher.publish(event)   │
│  ├─ Serialize to JSON      │
│  └─ Add metadata           │
└────────┬───────────────────┘
         │
         ↓
┌────────────────────────────┐
│ RabbitMQ Exchange          │
│  (Fanout)                  │
└────────┬───────────────────┘
         │
         ├─────────────┬──────────────┐
         ↓             ↓              ↓
    ┌────────┐   ┌────────┐    ┌────────┐
    │Queue A │   │Queue B │    │Queue C │
    │(durable)   │(durable)    │(durable)
    └────┬───┘   └────┬───┘    └────┬───┘
         │            │             │
         ↓            ↓             ↓
    ┌────────┐   ┌────────┐    ┌────────┐
    │Consumer│   │Consumer│    │Consumer│
    │   A    │   │   B    │    │   C    │
    └────┬───┘   └────┬───┘    └────┬───┘
         │            │             │
         ↓            ↓             ↓
    ┌────────┐   ┌────────┐    ┌────────┐
    │ ACK    │   │ NACK   │    │ ACK    │
    │Success │   │ Retry  │    │Success │
    └────────┘   └────┬───┘    └────────┘
                      │
                      ↓
                 ┌─────────────┐
                 │ Dead Letter │
                 │ Queue (DLQ) │
                 └──────┬──────┘
                        │
                        ↓ (Scheduled retry)
                 ┌─────────────┐
                 │ Retry Logic │
                 │ - Max: 24   │
                 │ - Exp backoff│
                 └─────────────┘
```

### Event Configuration Pattern

Each domain that consumes events extends `AbstractRabbitMQConsumerConfiguration`:

```kotlin
@Configuration
class ListingMQConfiguration(
    publisher: Publisher,
    consumer: Consumer,
    connectionFactory: ConnectionFactory,
    @Value("\${listing.queue}") private val queue: String,
    @Value("\${listing.dlq}") private val dlq: String,
    @Value("\${listing.exchange}") private val exchangeName: String,
) : AbstractRabbitMQConsumerConfiguration(publisher, consumer, connectionFactory) {

    @PostConstruct
    fun init() {
        setupExchange(exchangeName)
        setupQueue(queue, dlq, exchangeName)
        setupConsumer(queue, consumer, 4)  // 4 threads
    }

    @Scheduled(cron = "0 */15 * * * *")  // Every 15 minutes
    fun processRoomDlq() {
        processDlq(queue, dlq)
    }
}
```

## Data Flow Examples

### Example 1: Creating a Listing

```
User submits form in koki-portal
    ↓
┌─────────────────────────────┐
│ Portal Controller           │
│  settingsListingService     │
│  .createListing(form)       │
└──────────┬──────────────────┘
           │
           ↓ (HTTP POST)
┌─────────────────────────────┐
│ KokiListings SDK            │
│  .create(request)           │
│   → POST /v1/listings       │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ koki-server                 │
│ ListingEndpoints.create()   │
│  ├─ Extract X-Tenant-ID     │
│  ├─ Validate JWT            │
│  └─ Call service            │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ ListingService.create()     │
│  ├─ Map DTO → Entity        │
│  ├─ Set status = DRAFT      │
│  ├─ Set tenantId            │
│  └─ Save to DB              │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ MySQL Database              │
│  INSERT INTO listing        │
│   (tenant_id, status, ...)  │
└─────────────────────────────┘
```

### Example 2: Publishing a Listing with AI

```
User clicks "Publish" button
    ↓
┌─────────────────────────────┐
│ KokiListings.publish(id)    │
│   → POST /v1/listings/{id}  │
│            /publish         │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ ListingService.publish()    │
│  ├─ Load listing            │
│  ├─ Run validation rules    │
│  └─ Call ListingPublisher   │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ ListingPublisher.publish()  │
│  ├─ Load listing files      │
│  ├─ Compute image quality   │
│  ├─ Select best hero image  │
│  ├─ Create AI agent         │
│  │  (Gemini/Deepseek/Kimi)  │
│  ├─ Generate title          │
│  ├─ Generate description    │
│  ├─ Update status=ACTIVE    │
│  └─ Save listing            │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ Publish Event               │
│  ListingStatusChangedEvent  │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ RabbitMQ Queue              │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ Multiple Consumers          │
│  ├─ EmailConsumer           │
│  │  (send notifications)    │
│  ├─ TrackingConsumer        │
│  │  (analytics)             │
│  └─ LeadConsumer            │
│     (update related leads)  │
└─────────────────────────────┘
```

### Example 3: File Upload Flow

```
User uploads property image
    ↓
┌─────────────────────────────┐
│ KokiFiles.upload()          │
│   → POST /v1/files          │
│   (multipart/form-data)     │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ FileEndpoints.upload()      │
│  ├─ Validate file           │
│  ├─ Extract metadata        │
│  └─ Call FileService        │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ FileService.upload()        │
│  ├─ Generate unique ID      │
│  ├─ Detect content type     │
│  ├─ Extract info (text, etc)│
│  ├─ Store file via          │
│  │  StorageService          │
│  └─ Save metadata to DB     │
└──────────┬──────────────────┘
           │
           ├─────────────────────┐
           ↓                     ↓
┌────────────────────┐  ┌──────────────────┐
│ MySQL Database     │  │ Storage Backend  │
│  file metadata     │  │  S3 or Local     │
└────────────────────┘  └──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ Publish Event               │
│  FileUploadedEvent          │
└──────────┬──────────────────┘
           │
           ↓
┌─────────────────────────────┐
│ Listing Domain              │
│  ListingFileUploadedHandler │
│  ├─ Link file to listing    │
│  └─ Update listing status   │
└─────────────────────────────┘
```

## Deployment Architecture

### Production Deployment

```
                    ┌─────────────────┐
                    │  Load Balancer  │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
            ↓                ↓                ↓
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │ koki-server  │  │ koki-server  │  │ koki-server  │
    │  Instance 1  │  │  Instance 2  │  │  Instance N  │
    └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
           │                 │                 │
           └─────────────────┼─────────────────┘
                             │
            ┌────────────────┼────────────────┬────────────────┐
            │                │                │                │
            ↓                ↓                ↓                ↓
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │    MySQL     │  │   RabbitMQ   │  │    Redis     │  │     S3       │
    │   Database   │  │ Message Queue│  │    Cache     │  │   Storage    │
    └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘


    Separate Deployments:
    ┌──────────────┐  ┌────────────────────┐  ┌──────────────────────┐
    │ koki-portal  │  │ koki-portal-public │  │ koki-tracking-server │
    │  (Admin UI)  │  │  (Public Website)  │  │    (Analytics)       │
    └──────────────┘  └────────────────────┘  └──────────────────────┘
```

### Environment Configuration

Each environment uses Spring profiles:
- `default`: Local development (local MySQL, local storage)
- `test`: Integration tests (test DB, in-memory cache)
- `prod`: Production (S3, Redis, RabbitMQ)

## Key Architectural Principles

1. **Single Responsibility**: Each domain handles one business capability
2. **Dependency Inversion**: Depend on abstractions (interfaces), not implementations
3. **Open/Closed**: Extensible via plugins (storage, cache, AI providers)
4. **Tenant Isolation**: Strict multi-tenancy at all layers
5. **Event-Driven**: Asynchronous processing for scalability
6. **API-First**: All functionality exposed via REST APIs
7. **Type Safety**: Kotlin + strong typing throughout
8. **Test Coverage**: 92-98% enforced via JaCoCo
9. **Infrastructure as Code**: Configuration via YAML/properties
10. **Cloud-Native**: Stateless services, external state stores

## Technology Stack Summary

| Layer | Technologies |
|-------|-------------|
| **Language** | Kotlin 2.3.10, Java 17 |
| **Framework** | Spring Boot 4.0.2, Spring Security 7.0.2 |
| **Database** | MySQL 9.6, Flyway migrations |
| **ORM** | Spring Data JPA, Hibernate 6.x |
| **Caching** | Redis 7.0+ (Lettuce client) |
| **Messaging** | RabbitMQ 4.2+ (AMQP) |
| **Storage** | AWS S3 / Local filesystem |
| **Web UI** | Thymeleaf 3.1.3, Server-side rendering |
| **API Docs** | SpringDoc OpenAPI 3.0.1 |
| **Testing** | JUnit 6.0.1, Mockito Kotlin |
| **Code Quality** | ktlint 1.8.0, JaCoCo 0.8.14 |
| **AI/LLM** | Google Gemini, Deepseek, Kimi |
| **Translation** | AWS Translate |
| **Build** | Maven 3.9+ |
| **CI/CD** | GitHub Actions |
| **Deployment** | Heroku (Procfile), Docker-ready |

---

*This architecture supports a scalable, multi-tenant real estate platform with clear separation of concerns, event-driven processing, and flexible infrastructure integration.*
