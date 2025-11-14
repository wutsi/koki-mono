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

The **koki-sdk** module is a Kotlin client library providing type‑safe wrappers around Koki REST APIs: accounts,
listings, offers, files, messages, contacts, notes, tenants, roles, configuration, invitations, users, and reference
data. It streamlines third‑party and internal service integration by abstracting HTTP, URL composition, query
parameter encoding, authentication header management, pagination, and multipart file upload handling.

Design goals:

- **Developer Productivity**: Eliminate boilerplate HTTP request construction
- **Consistency**: Reuse shared DTOs from `koki-dto` to ensure uniform payload schemas
- **Robustness**: Centralize error handling and response mapping
- **Discoverability**: Provide domain-focused classes (e.g., `KokiListings`, `KokiAccounts`) with clear method names
- **Extensibility**: Encapsulate URL building and resource patterns to allow future domain expansion easily

Non-goals:

- Executing business logic or persistence operations
- Replacing full-featured HTTP clients for arbitrary endpoints outside Koki API scope
- Providing offline caching or synchronization logic

## Project Structure

```
modules/koki-sdk/
├── pom.xml
├── ARCHITECTURE.md
├── README.md
└── src/
    ├── main/
    │   ├── kotlin/com/wutsi/koki/sdk/
    │   │   ├── AbstractKokiModule.kt    (Base class: common HTTP helpers, RestTemplate wrapper)
    │   │   ├── URLBuilder.kt            (Centralized path & query composition)
    │   │   ├── KokiAccounts.kt          (Accounts domain client)
    │   │   ├── KokiAgent.kt             (Agent/automation domain client)
    │   │   ├── KokiAuthentication.kt    (Authentication flows)
    │   │   ├── KokiConfiguration.kt     (Module/config metadata retrieval)
    │   │   ├── KokiContacts.kt          (Contacts/CRM operations)
    │   │   ├── KokiFiles.kt             (File upload/download operations)
    │   │   ├── KokiInvitations.kt       (Invitation management)
    │   │   ├── KokiLead.kt              (Lead domain interactions)
    │   │   ├── KokiListings.kt          (Listing search & retrieval)
    │   │   ├── KokiMessages.kt          (Messaging domain access)
    │   │   ├── KokiModules.kt           (Activated modules / feature flags)
    │   │   ├── KokiNotes.kt             (Note management)
    │   │   ├── KokiOffer.kt             (Offer operations)
    │   │   ├── KokiOfferVersion.kt      (Offer version history)
    │   │   ├── KokiRefData.kt           (Reference data lookup)
    │   │   ├── KokiRoles.kt             (Role/permission retrieval)
    │   │   ├── KokiTenants.kt           (Tenant operations)
    │   │   ├── KokiTypes.kt             (Type classification endpoints)
    │   │   ├── KokiUsers.kt             (User account operations)
    │   └── resources/
    └── test/
        ├── kotlin/                      (Test clients & integration tests)
        └── resources/
```

Structure principles:

- **Class-per-domain**: Each API surface isolated for clarity & modular evolution
- **Shared base**: `AbstractKokiModule` handles repetitive HTTP boilerplate (headers, serialization, upload)
- **Central URL composition**: `URLBuilder` prevents scattered string concatenation errors
- **Immutable DTO usage**: All payloads exchanged using `koki-dto` contracts

## High-Level System Diagram

```
+------------------------------+        +------------------------------+
| Application / Integration    |        | Koki Platform Services       |
| (Portal, CLI, External App)  |  --->  | (koki-server, portals, etc.) |
|  Uses koki-sdk               |        |  Expose REST endpoints       |
+---------------+--------------+        +---------------+--------------+
                | HTTP (JSON)                           ^
                v                                       |
          +------------------+                          |
          |   koki-sdk       |  <-----------------------+
          |  Domain Clients  |     Responses (DTO JSON)
          +--------+---------+
                   |
                   v
            +--------------+
            | RestTemplate |
            +------+-------+
                   |
                   v
              External HTTP
```

Flow description:

1. Consumer code invokes a domain client method (e.g., `KokiListings.listings(...)`).
2. Client uses `URLBuilder` to compose path + query parameters.
3. `RestTemplate` performs HTTP request; headers (Authorization, Tenant) applied by configuration/interceptors.
4. Response JSON mapped to DTOs from `koki-dto`.
5. Errors parsed and surfaced as typed exceptions (e.g., `HttpClientErrorException`).

Key boundaries:

- No persistence or caching internal to SDK.
- No direct message queue or storage interactions.
- Pure HTTP abstraction reliant on Spring Web provided dependency.

## Core Components

### AbstractKokiModule

- Base class offering helper methods for GET/POST/PUT/DELETE abstraction, multipart handling, and uniform header
  addition.
- Central place for future cross-cutting enhancements (retry, circuit breaking via wrappers).

### URLBuilder

- Responsible for building endpoint URLs consistently.
- Encodes query parameters: primitives, collections, pagination (`limit`, `offset`).
- Shields consumers from manual encoding mistakes.

### Domain Clients (Koki* classes)

- Each encapsulates CRUD/search operations for a specific domain (Accounts, Listings, Offers, Files, etc.).
- Methods return strongly typed DTO responses.
- Enforce consistent naming (e.g., `create`, `update`, `delete`, `listings`, `account`).

### DTO Integration

- All request/response models imported from `koki-dto`.
- No duplication of model definitions.

### Authentication Handling

- Authorization headers normally added outside or via wrapper; SDK design encourages injection of a
  preconfigured `RestTemplate`.

### Multipart Support (Files)

- `KokiFiles` wraps file upload logic (content type, form/multipart boundaries).

### Pagination & Filtering

- Methods expose limit/offset + filter parameters for scalable queries.

### Error Handling

- Relies on Spring's `RestTemplate` exceptions; potential future wrapper for standardized error envelope mapping.

## Data Stores

None. The SDK does not persist data nor manage caches. All state is transient within HTTP calls and returned DTOs.

## External Integrations / APIs

### Primary Dependency: Koki REST API

- Base URL configured by consumer code (e.g., `https://api.koki.example`).
- Endpoints organized by domain: `/accounts`, `/listings`, `/offers`, etc.

### Libraries (Compile-Time / Provided)

- **Spring Web (provided)**: `RestTemplate` for HTTP communication.
- **koki-dto**: Shared data contracts.
- **koki-platform**: Utility/services reused (e.g., tenant or security helpers if integrated).
- **Commons IO**: Stream/file handling convenience.

No direct usage of message queues, object storage, or translation services—those remain server-side concerns.

## Deployment & Infrastructure

### Build & Packaging

- Built via Maven producing `com.wutsi.koki:koki-sdk:VERSION_NUMBER` JAR.
- Published to GitHub Packages; consumed like any other Maven dependency.

### CI/CD

- Workflows: `koki-sdk-master.yml`, `koki-sdk-pr.yml` for build/test and PR validation.
- Potential coverage badge not present at time of writing (add if generated).

### Consumption

- Dependency declaration (example):

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-sdk</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

- Consumers construct and inject a configured `RestTemplate` (auth, tenant headers, timeouts) then instantiate domain
  clients.

### Configuration

- No mandatory internal configuration beyond base URL; recommended to externalize:
    - API base URL
    - Access token acquisition strategy
    - Tenant identifier resolution

### Observability

- Logging and tracing delegated to consuming application; SDK remains thin to avoid opinionated instrumentation.

## Security Considerations

### Authentication

- Relies on bearer token header (`Authorization: Bearer <token>`); token retrieval handled outside SDK.
- Tenant header (e.g., `X-Tenant-ID`) applied by consumer interceptors.

### Data Integrity

- DTO schemas ensure type safety; misuse prevented by compile-time model constraints.

### Sensitive Data

- SDK should avoid logging raw payloads or PII; recommend consumers sanitize logs.

### Error Surfacing

- Server-side authorization failures (401/403) surfaced as exceptions; consumers responsible for retry/refresh logic.

### Hardening Recommendations (Consumer Side)

1. Implement timeouts & retry policies on `RestTemplate` (e.g., via `ClientHttpRequestFactory`).
2. Rate limit client operations if automating large batches.
3. Rotate access tokens and avoid embedding static secrets.
4. Validate input before invoking SDK methods to reduce 4xx responses.


