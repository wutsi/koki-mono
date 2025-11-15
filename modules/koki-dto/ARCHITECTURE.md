# ARCHITECTURE

## Table of Content

- [Overview](#overview)
- [Project Structure](#project-structure)
- [High-Level System Diagram](#high-level-system-diagram)
- [Core Components](#core-components)
- [Data Stores](#data-stores)
- [Deployment & Infrastructure](#deployment--infrastructure)
- [Security Considerations](#security-considerations)

## Overview

The **koki-dto** module is the authoritative data contract library for the Koki platform. It supplies immutable Kotlin
data classes (DTOs) for requests, responses, events, errors, security, reference data, and cross-domain helpers. These
contracts are consumed compile-time by backend services (koki-server), portals (koki-portal, koki-portal-public),
chatbots, tracking services, and the client SDK (koki-sdk).

Evolution & Versioning (integrated): Changes prioritize backward compatibility by introducing new optional fields rather
than repurposing existing ones. Field removals/renames are avoided unless coordinated across all dependents using a
MAJOR version increment. Deprecated fields are retained for a transition period and documented. Snapshot artifacts (
-SNAPSHOT) enable rapid iteration; releases follow semantic versioning (MAJOR.MINOR.PATCH). Cross-module compatibility
is validated in CI when shared dependency versions are bumped.

Non-Goals: Executing business rules, persistence operations, service orchestration, external API calls, or managing
storage schemas/migrations.

Benefits: Eliminates schema drift; improves IDE discoverability and refactoring safety; standardizes validation and
error representation; enables consistent event typing and safer asynchronous integration.

## Project Structure

```
modules/koki-dto/
├── pom.xml
├── ARCHITECTURE.md
├── README.md
└── src/
    ├── main/
    │   ├── kotlin/com/wutsi/koki/
    │   │   ├── account/dto/
    │   │   │   └── event/
    │   │   ├── agent/dto/
    │   │   ├── common/dto/
    │   │   ├── contact/dto/
    │   │   ├── error/dto/
    │   │   ├── file/dto/
    │   │   │   └── event/
    │   │   ├── lead/dto/
    │   │   │   └── event/
    │   │   ├── listing/dto/
    │   │   │   └── event/
    │   │   ├── message/dto/
    │   │   │   └── event/
    │   │   ├── module/dto/
    │   │   ├── note/dto/
    │   │   │   └── event/
    │   │   ├── offer/dto/
    │   │   │   └── event/
    │   │   ├── refdata/dto/
    │   │   ├── security/dto/
    │   │   ├── tenant/dto/
    │   │   │   └── event/
    │   │   └── track/dto/
    │   │       └── event/
    │   └── resources/
    └── test/
        ├── kotlin/
        └── resources/
```

Structure principles: Package-by-domain isolation; `event` subfolder per domain for asynchronous lifecycle transitions;
naming conventions (`CreateXRequest`, `XCreatedEvent`, etc.); immutable data classes; validation annotations only on
inbound request DTOs; enums for bounded sets; cross-cutting primitives centralized in `common/dto`.

## High-Level System Diagram

```
+------------------+        +--------------------+        +------------------+
|  Client SDK      | -----> |  Backend Services  | -----> |  Message Queue   |
|  (koki-sdk)      |        |  (koki-server,     |        |  (RabbitMQ)      |
|                  | <----- |   portals, etc.)   | <----- |  Consumers       |
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

Flow Summary: Request DTO serialized by client → validated & deserialized by service → business logic executes →
Response DTO returned; Event DTOs published for async processing; consumers deserialize events for side-effects.

Boundary: koki-dto is purely a compile-time artifact; no runtime I/O or persistence.

## Core Components

### Request DTOs

Purpose: Define inbound payload schema with validation constraints. Interaction: Used at controller boundaries with
automatic validation.

### Response DTOs

Purpose: Represent outbound payloads (details, summaries, paginated sets). Interaction: Serialized back to clients; no
validation annotations.

### Event DTOs

Purpose: Capture domain lifecycle transitions for asynchronous workflows. Interaction: Published by services; consumed
by downstream processors.

### Error Models

Purpose: Standard error envelope and codes for uniform failure representation. Interaction: Emitted by services; clients
parse for diagnostics.

### Security DTOs

Purpose: Model authentication flows and token claim access. Interaction: Consumed by auth layers to decode/represent
principals.

### Common DTOs

Purpose: Cross-domain primitives (references, import responses, headers). Interaction: Reused across domains to reduce
duplication.

### Reference Data DTOs

Purpose: Represent relatively static lookup data (locations, attribute types). Interaction: Served by backend;
referenced in other DTOs.

### Domain-Specific DTOs

Purpose: Encapsulate business concepts (accounts, listings, offers, leads, contacts, messages, notes, files, tenants,
tracking). Interaction: Shared across services for consistency.

### Enumerations & Constants

Purpose: Typed value sets preventing magic strings; ensure safe evolution. Interaction: Embedded in DTO fields and logic
in consuming services.

## Data Stores

None. The module is stateless and persistence-agnostic.

## Deployment & Infrastructure

Build & Package:

```bash
mvn clean install
```

Produces artifact: `com.wutsi.koki:koki-dto:VERSION_NUMBER`.

Distribution: Published to internal Maven repository (GitHub Packages). Snapshot (`-SNAPSHOT`) for iteration; semantic
versioning for releases.

Runtime Footprint: None. No environment variables, ports, external services required directly.

Consumption: Add dependency in downstream `pom.xml` and import domain packages.

## Security Considerations

Validation: Constraint annotations reject malformed input early when applied with `@Valid` in consuming services.

JWT Handling: Decoder-related DTOs require proper algorithm configuration in consumers; transport secured via HTTPS.

Privacy & PII: DTOs may include personal data; consumers must avoid logging raw PII and apply encryption at rest/in
transit.

Backward Compatibility: Prefer additive changes; coordinate MAJOR version increments on breaking changes.

Immutability: Kotlin data classes discourage shared mutable state; nullability explicit only for optional semantics.

Dependency Hygiene: Minimal dependencies reduce attack surface; upgrades managed centrally in parent POM.
