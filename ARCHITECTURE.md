# ARCHITECTURE

## Table of Content

- [Overview](#overview)
- [Project Structure](#project-structure)
- [High-Level System Diagram](#high-level-system-diagram)
- [Core Components](#core-components)
    - [Backend API (koki-server)](#backend-api-koki-server)
    - [Infrastructure Library (koki-platform)](#infrastructure-library-koki-platform)
    - [Data Transfer Objects (koki-dto)](#data-transfer-objects-koki-dto)
    - [Client SDK (koki-sdk)](#client-sdk-koki-sdk)
    - [Administrative Portal (koki-portal)](#administrative-portal-koki-portal)
    - [Public Portal (koki-portal-public)](#public-portal-koki-portal-public)
    - [Tracking Server (koki-tracking-server)](#tracking-server-koki-tracking-server)
    - [Chatbot Integrations (koki-chatbot-*)](#chatbot-integrations-koki-chatbot-)
- [Data Stores](#data-stores)
- [Deployment & Infrastructure](#deployment--infrastructure)
- [Security Considerations](#security-considerations)

## Overview

This document describes the high-level architecture of the **koki-mono** monorepo. The platform provides a multi-tenant
real estate SaaS solution composed of modular services and libraries that handle property listings, leads, messaging,
offers, tenant operations, analytics, and public/user-facing presentation layers. The architecture emphasizes separation
of concerns, shared infrastructure, observability, multi-tenancy, and pluggable providers for storage, messaging,
caching, translation, and AI.

## Project Structure

```
koki-mono/
├── pom.xml                     # Root aggregator POM
├── README.md                   # Root documentation
├── ARCHITECTURE.md             # This document
├── CONTRIBUTING.md             # Contribution guidelines
├── DEVELOP.md                  # Development setup instructions
├── TESTING.md                  # Testing strategy and commands
├── LICENSE.md                  # License
├── docs/                       # Additional design docs
├── modules/                    # All submodules
│   ├── koki-dto/               # Shared DTO definitions
│   ├── koki-platform/          # Infrastructure abstractions (storage, cache, mq, logging, etc.)
│   ├── koki-sdk/               # Client-side SDK for external integrations / consumption
│   ├── koki-server/            # Core backend REST APIs (multi-tenant business logic)
│   ├── koki-portal/            # Administrative server‑side rendered web portal
│   ├── koki-portal-public/     # Public-facing portal for listing discovery
│   ├── koki-tracking-server/   # Event ingestion + KPI aggregation service
│   ├── koki-chatbot/           # (Optional) Generic chatbot integration layer
│   ├── koki-chatbot-telegram/  # (Optional) Telegram bot integration
│   └── koki-chatbot-messenger/ # (Optional) Messenger bot integration
└── .github/                    # Workflows, badges, automation
```

## High-Level System Diagram

```
                 ┌────────────────────────────────────────────────┐
                 │                    Users                       │
                 │  - Admin (Portal)   - Public Visitors          │
                 │  - API Clients      - Chatbot Users            │
                 └───────────────┬────────────────────────────────┘
                                 │
                                 ▼
                      ┌──────────────────────┐
                      │  Entry Points        │
                      │  - koki-portal       │
                      │  - koki-portal-public│
                      │  - Chatbots          │
                      │  - External Clients  │
                      └──────────┬───────────┘
                                 │ HTTP / Web / Bot
                                 ▼
                      ┌──────────────────────┐
                      │   koki-server        │  REST APIs
                      │ (Business Services)  │  Auth, Multi-Tenancy
                      └──────────┬───────────┘
                                 │
        ┌────────────────────────┴────────────────────────┐
        │                                                 │
        ▼                                                 ▼
┌──────────────────────┐                       ┌──────────────────────┐
│  koki-platform       │  Infrastructure       │  koki-tracking-server │
│  (Shared Library)    │  (storage/cache/mq)   │  (Event → KPI)        │
└──────────┬───────────┘                       └──────────┬───────────┘
           │ Uses                                     Publishes / Reads
           ▼                                              ▼
     ┌─────────────┐   ┌────────────┐   ┌─────────────┐   ┌────────────┐
     │  MySQL       │   │  Redis     │   │ RabbitMQ     │  │   S3        │
     └─────────────┘   └────────────┘   └─────────────┘   └────────────┘
```

## Core Components

### Backend API (koki-server)

**Purpose:** Provides tenant-aware REST APIs for accounts, listings, offers, leads, messaging, files, payments, and
configuration.
**Key Functions:** CRUD operations, transactional business logic, authentication/authorization enforcement, scheduled
tasks, payment integration.
**Interactions:** Consumed by portals, chatbots, SDK clients; uses koki-platform for storage, messaging, caching.

### Infrastructure Library (koki-platform)

**Purpose:** Supplies reusable abstractions (storage, cache, RabbitMQ, logging, templating, translation, AI provider
integration, tenant/context propagation).
**Key Functions:** Provider selection via configuration; unified APIs; structured logging; multi-tenant context
handling.
**Interactions:** Compile-time dependency of all runtime services (server, portals, tracking, chatbots).

### Data Transfer Objects (koki-dto)

**Purpose:** Centralized strongly-typed data contracts (Kotlin DTOs) shared across services.
**Key Functions:** Consistent request/response schemas; enum/type reuse; versioned models.
**Interactions:** Imported by server, SDK, tracking, and portal modules for serialization consistency.

### Client SDK (koki-sdk)

**Purpose:** Simplifies consuming koki-server APIs from external services or internal modules.
**Key Functions:** Feign/HTTP client wrappers, authentication handling, error translation, retries (where configured).
**Interactions:** Used by portals and possibly automation jobs to interact with backend.

### Administrative Portal (koki-portal)

**Purpose:** Server-side rendered web interface for tenant operations and internal management.
**Key Functions:** Form handling, session management, HTML rendering (Thymeleaf), integration via koki-sdk, role-based
access.
**Interactions:** Calls koki-server APIs; optionally publishes tracking events.

### Public Portal (koki-portal-public)

**Purpose:** Public-facing site for listing discovery and content access.
**Key Functions:** SEO-friendly pages, anonymous browsing, public asset delivery, lightweight caching and tracking event
generation.
**Interactions:** Queries koki-server via SDK; emits tracking events to RabbitMQ; reads cached content.

### Tracking Server (koki-tracking-server)

**Purpose:** Ingests tracking events, enriches them (bot/device/source/country), persists batches (CSV/object storage),
and aggregates KPI metrics.
**Key Functions:** Queue consumption, enrichment pipeline, scheduled KPI generation, dead-letter isolation.
**Interactions:** Receives events from portals/server/chatbots; stores raw data & KPI outputs; exposes aggregated
metrics for analytics.

### Chatbot Integrations (koki-chatbot-*)

**Purpose:** Channel-specific conversational interfaces (Telegram, Messenger) for user interaction and event generation.
**Key Functions:** Command parsing, conversation state, backend data retrieval via koki-server, tracking events
submission.
**Interactions:** Invoke koki-server APIs; publish tracking events; display responses to end users.

## Data Stores

| Store      | Purpose                                             | Accessed By                                       | Notes                                 |
|------------|-----------------------------------------------------|---------------------------------------------------|---------------------------------------|
| MySQL      | Core transactional data (entities)                  | koki-server                                       | Managed via JPA/Flyway by server only |
| Redis      | Caching, ephemeral acceleration                     | portals, server, tracking (optional)              | TTL & namespacing via tenant context  |
| RabbitMQ   | Async messaging + tracking events                   | portals, server, chatbots, tracking server        | Retry & DLQ supported                 |
| S3 / Local | Object/file storage (uploads, reports, CSV batches) | server, tracking server, portals                  | Provider selected by configuration    |
| CSV Assets | Raw tracking batches & KPI output                   | tracking server (read/write), analytics consumers | Stored in object storage folders      |

(Platform module provides abstractions; ownership resides with runtime services.)

## Deployment & Infrastructure

**Build & Package:**

```bash
mvn clean install
```

Generates artifacts for all modules. Library modules published to GitHub Packages; runtime services deployable as
executable JARs.

**Runtime Profiles (typical):**

- `local`: Rapid cron schedules, local storage, verbose logging
- `test`: Integration testing, controlled delays
- `prod`: Optimized resource usage, cloud storage/services

**Service Ports (defaults):**

- koki-server: 8080
- koki-portal: 8081
- koki-portal-public: 8082
- koki-tracking-server: 8083

**Scalability:**

- Horizontal scaling of stateless services behind load balancers
- Tracking server consumers scale by adding queue listeners
- Caching reduces pressure on database / API for portals

**Observability:**

- Structured key-value logging (tenant, trace, correlation IDs)
- Actuator endpoints (health, info, metrics, scheduled tasks)
- Tracing/context propagation via platform utilities

**Configuration Management:**

- Environment variables and externalized YAML profiles
- Feature flags (debug/feature packages) toggle non-critical functionality

**Resilience:**

- RabbitMQ DLQ for poison messages
- Batch persistence (tracking) decouples ingestion throughput from storage latency

## Security Considerations

**Authentication & Authorization:**

- JWT tokens validated by backend services; portals use session/token bridging.
- Tenant and user claims drive authorization decisions.

**Multi-Tenancy Isolation:**

- Tenant ID propagated through API requests, cache keys, storage paths, and log context.
- No cross-tenant data exposure by design in server data access layer.

**Data Protection:**

- HTTPS mandatory in production.
- S3 bucket encryption recommended; secrets externalized (no hardcoded credentials).
- Limited PII logged; structured logging filters sensitive fields.

**Input Validation & Integrity:**

- Bean validation for API DTOs and form inputs.
- Tracking pipeline excludes invalid/bot events from KPI aggregation.

**Least Privilege & Secrets:**

- Scoped credentials for RabbitMQ, Redis, S3.
- Rotate API keys (Stripe/PayPal/AWS) externally.

**Hardening Recommendations:**

1. Implement rate limiting at gateway layer.
2. Enforce strong JWT signing algorithms with key rotation.
3. Apply WAF rules for public portals.
4. Monitor DLQ size and abnormal event ingestion patterns.
5. Integrate vulnerability scanning in CI and renovate dependency updates.

**Audit & Monitoring:**

- Correlation IDs enable traceability across services.
- KPI aggregation provides visibility into usage & engagement trends.

