# ARCHITECTURE

## Table of Content

- [Overview](#overview)
    - [Evolution & Versioning](#evolution--versioning)
- [Project Structure](#project-structure)
- [High-Level System Diagram](#high-level-system-diagram)
- [Core Components](#core-components)
- [Data Stores](#data-stores)
- [External Integrations / APIs](#external-integrations--apis)
- [Deployment & Infrastructure](#deployment--infrastructure)
- [Security Considerations](#security-considerations)

<!-- NOTE: Update the ToC when headings change. -->

## Overview

The **koki-dto** module is the central, authoritative data contract library for the Koki platform. It supplies immutable
Kotlin data classes (DTOs) for requests, responses, events, errors, security, reference data, and cross-domain helpers.
These contracts are consumed compile‑time by backend services (**koki-server**), portals (**koki-portal**, *
*koki-portal-public**), chatbots, tracking services, and the client SDK (**koki-sdk**).

Key characteristics:

- Single source of truth for REST and asynchronous (event) payload schemas
- Immutable, type‑safe Kotlin data classes with declarative Jakarta Validation constraints on inbound request models
- Domain‑partitioned package structure (account, listing, offer, lead, tenant, security, etc.) for clear separation of
  concerns
- Event model subpackages enabling strongly‑typed, message‑queue based, asynchronous workflows (e.g., RabbitMQ consumers
  in other modules)
- Minimal runtime footprint: no networking, persistence, caching, or business logic contained here
- Explicit evolution strategy favoring additive, backward‑compatible changes (never silently repurposing fields)
- Security contract objects (JWT principal / decoder, login request/response) enabling consistent authentication
  semantics across services

Non‑Goals:

- Executing business rules or persistence operations
- Implementing service orchestration or external API calls
- Managing storage schemas or migrations

Benefits:

- Eliminates schema drift across independently deployed services
- Improves IDE discoverability and refactoring safety
- Standardizes validation and error representation
- Enables consistent event typing and safer asynchronous integration

### Evolution & Versioning

Approach focuses on backward compatibility:

- Additive changes first: introduce new optional fields instead of repurposing existing ones
- Field removals/renames avoided unless coordinated across all dependents with a MAJOR version increment
- Deprecated fields documented and retained for a transition period before removal
- Snapshot artifacts (`-SNAPSHOT`) enable rapid iteration; releases follow semantic versioning (MAJOR.MINOR.PATCH)
- Cross-module compatibility validated by CI on upgrade of shared dependency versions

## Project Structure

```
modules/koki-dto/
├── pom.xml
├── ARCHITECTURE.md
├── README.md
└── src/
    ├── main/
    │   ├── kotlin/com/wutsi/koki/
    │   │   ├── account/dto/      (Account & identity domain)
    │   │   │   └── event/
    │   │   ├── agent/dto/        (Automation / AI)
    │   │   ├── common/dto/       (Cross-cutting primitives)
    │   │   ├── contact/dto/      (CRM / contacts)
    │   │   ├── error/dto/        (Standardized error modeling)
    │   │   ├── file/dto/         (File metadata)
    │   │   │   └── event/
    │   │   ├── lead/dto/         (Lead management)
    │   │   │   └── event/
    │   │   ├── listing/dto/      (Property listings)
    │   │   │   └── event/
    │   │   ├── message/dto/      (Messaging)
    │   │   │   └── event/
    │   │   ├── module/dto/       (Module configuration)
    │   │   ├── note/dto/         (Notes)
    │   │   │   └── event/
    │   │   ├── offer/dto/        (Offers)
    │   │   │   └── event/
    │   │   ├── refdata/dto/      (Reference data)
    │   │   ├── security/dto/     (Auth/JWT contracts)
    │   │   ├── tenant/dto/       (Multi-tenancy)
    │   │   │   └── event/
    │   │   └── track/dto/        (Tracking / analytics)
    │   │       └── event/
    │   └── resources/
    └── test/
        ├── kotlin/
        └── resources/
```

Structure principles:

- Package‑by‑domain: Each business domain isolated for readability & modular evolution
- `dto/event` subfolders: Dedicated namespace for asynchronous lifecycle transitions (Created/Updated/Deleted events)
- Naming
  conventions: `CreateXRequest`, `UpdateXRequest`, `SearchXResponse`, `XSummary`, `XCreatedEvent`, `XUpdatedEvent`, `XDeletedEvent`
- Immutable data classes: Prefer non‑nullable fields; nullable only where semantically optional
- Validation annotations only on inbound request DTOs (responses/events are server‑controlled and unvalidated)
- Enumerations codify bounded value sets (object types, statuses, roles, etc.)
- Cross‑cutting primitives (ObjectReference, ImportResponse, HttpHeader) centralized in `common/dto`
- Reference data (countries, cities, attribute types, configuration forms) isolated in `refdata/dto`

## High-Level System Diagram

Textual (C4-ish) context depiction:

```
+------------------+        +--------------------+        +------------------+
|  Client SDK      | -----> |  Backend Services  | -----> |  Message Queue   |
|  (koki-sdk)      |        |  (koki-server,     |        |  (RabbitMQ)      |
|                  | <----- |   chatbots, etc.)  | <----- |  Consumers       |
+------------------+        +--------------------+        +------------------+
         ^                           ^    ^                         ^
         |                           |    |                         |
         | (Compile-time dependency) |    | (Compile-time)          |
         |                           |    |                         |
                    +----------------------------------+
                    |            koki-dto              |
                    |  (Shared Data Contracts Library) |
                    +----------------------------------+
```

Data & Event Flow:

1. Client or portal constructs a Request DTO (using validation annotations) and serializes to JSON
2. Service deserializes JSON to the same Request DTO, triggers Jakarta Validation
3. Business logic (outside koki-dto) processes; Response DTO assembled and serialized
4. For asynchronous changes, an Event DTO is created and published to the queue by service code
5. Downstream consumers deserialize Event DTO and execute side‑effects (notifications, indexing, analytics)

Boundary Emphasis:

- **koki-dto** is purely a compile‑time artifact; it does not emit, receive, or store data at runtime
- All inbound/outbound I/O, persistence, and queue operations are implemented by consuming modules

## Core Components

### 1. Request DTOs

- Define inbound payload schema & constraints (`@NotNull`, `@NotEmpty`, `@Size`, `@Email`, `@Min`, `@Max`)
- Used at controller boundaries for automatic validation in Spring (`@Valid`)
- Express intent (Create, Update, Search) via naming

### 2. Response DTOs

- Server‑controlled payload shapes returned to clients (entity details, summaries, paginated results)
- Omit validation annotations; serialization ensures integrity
- Provide consistent pagination & metadata wrappers where applicable

### 3. Event DTOs

- Represent domain lifecycle transitions (Created/Updated/Deleted, Sent, etc.) for asynchronous processing
- Contain identifiers + timestamps; never include heavy/large embedded structures unnecessarily
- Enable loosely coupled inter‑service workflows (tracking, email, analytics)

### 4. Error Models

- Unified error envelope (**ErrorResponse**) with list of errors and parameter diagnostics
- Standardized codes via **ErrorCode** enumeration (e.g., validation failures, domain constraints)
- Parameter model distinguishes source (`PATH`, `QUERY`, `BODY`, `HEADER`)

### 5. Security DTOs

- **JWTDecoder** & **JWTPrincipal** abstractions for token verification & claim access
- **LoginRequest**/**LoginResponse** modeling authentication flows
- **ApplicationName** / subject type enumerations for scoping

### 6. Common DTOs

- Cross-domain primitives: **ObjectReference**, **ObjectType**, **ImportResponse**, **ImportMessage**, **HttpHeader**
  constants
- Promote reuse & reduce duplication of generic patterns

### 7. Reference Data DTOs

- Static / slowly changing lookup representations (countries, cities, attribute types)
- Form / configuration abstractions enabling dynamic portal rendering

### 8. Domain-Specific DTOs

- Accounts, Listings, Offers, Leads, Contacts, Messages, Notes, Files, Tenants, Tracking
- Each domain isolates: request/response/event enumerations & summary projections

### 9. Enumerations & Constants

- Provide strongly typed, discoverable value sets preventing magic strings
- Facilitate compile‑time safety during feature evolution

## Data Stores

None. The **koki-dto** module is stateless and persistence‑agnostic.

- No database, cache, filesystem, or external storage interactions
- Avoids coupling with ORM frameworks; enables flexible backend storage strategies
- Storage schemas, migrations, indexing handled exclusively by consuming services (e.g., **koki-server**)

## External Integrations / APIs

### Direct Dependencies (Compile-Time Only)

- **Jakarta Validation API** – Declarative constraint annotations on Request DTOs
- **Auth0 Java JWT** – Token decoding & signature verification utilities (via JWTDecoder/JWTPrincipal)
- **Kotlin Stdlib & JDK** – Language/runtime foundations

(Dependency versions are managed by the parent POM; when documenting, use `VERSION_NUMBER` placeholders if examples are
added.)

### Indirect Integrations (Implemented by Consumers)

- **RabbitMQ (or AMQP)** – Event transport; koki-dto supplies event models only
- **AWS S3** – File domain references validated in portals/server; DTO contains configuration fields
- **Payment Providers (Stripe/PayPal)** – Offer/transaction related DTO fields consumed by payment workflows
- **Email / Notification Services (SMTP, etc.)** – Event DTOs drive templated notifications
- **AI Services (e.g., Google Gemini)** – Agent/automation DTOs enabling inference requests/responses

### Upgrade & Dependency Hygiene

- Centralized parent POM + automated Renovate/Dependabot scanning
- Additive upgrades validated by consumer module CI pipelines

## Deployment & Infrastructure

### Build & Packaging

- **Build Tool:** Maven (inherits parent monorepo POM)
- **Lifecycle:**

```bash
mvn clean install
```

Produces artifact: `com.wutsi.koki:koki-dto:VERSION_NUMBER`

- **Artifact:** Lightweight JAR (no resources-heavy assets)

### Distribution

- Published to GitHub Packages (Maven repository)
- Snapshot versions (`-SNAPSHOT`) for iterative development; releases tagged semantically (MAJOR.MINOR.PATCH)

### CI/CD

- **Pull Request Workflow:** compile Kotlin sources, run unit tests (if present), dependency & vulnerability scan
- **Master / Release Workflow:** build, test, publish artifact, tag version, optionally trigger downstream notifications

### Consumption Requirements

- Maven `settings.xml` must include authenticated repository credentials (GitHub user/token)
- Downstream modules declare dependency: `com.wutsi.koki:koki-dto:VERSION_NUMBER`

### Runtime Footprint

- None; zero servers or service processes required
- No environment variables, ports, or infrastructure provisioning for this module

## Security Considerations

### Validation

- Declarative constraints ensure malformed input rejected early at controller boundary
- Consumers must annotate controller parameters with `@Valid` to activate automatic constraint checks
- Complex business invariants should be enforced in service layer, not embedded as implicit DTO logic

### JWT Handling

- **JWTDecoder** expects proper algorithm configuration; placeholder `Algorithm.none()` must be replaced in production
- Custom claims include tenant/user identifiers enabling multi‑tenant isolation
- Enforce HTTPS for token transport; apply standard expiration & rotation policies in consumers

### Dependency Hygiene

- Minimal dependency surface reduces attack vectors
- Automated vulnerability scanning & timely upgrades via CI tooling
- Avoid introduction of heavy transitive dependencies inside DTO definitions

### Privacy & PII

- DTOs may encode personal data (names, emails, phone numbers, addresses)
- Consumers must prevent logging of raw PII and ensure encryption at rest/in transit
- Favor explicit, purpose‑limited fields; avoid bundling unnecessary sensitive attributes

### Immutability & Safety

- Kotlin data classes discourage mutable shared state
- Prefer explicit nullability semantics; absence represented by `null` only where required
- Additive evolution: introduce new optional fields before deprecating existing ones; document deprecations clearly

### Backward Compatibility

- Avoid breaking changes (field removals/renames) without version strategy
- If unavoidable, coordinate synchronized release across dependent services and increment MAJOR version

### Production Hardening Guidance (for consumers)

- Replace default JWT algorithm with HMAC256 or RSA256
- Apply rate limiting & abuse detection at service boundaries (outside this module)
- Implement structured logging filters redacting PII field values
