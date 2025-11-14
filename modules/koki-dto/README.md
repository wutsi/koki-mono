# koki-dto

## Overview

A strongly-typed, immutable Kotlin DTO library providing the shared request, response, event, error, security, and
reference data contracts for the Koki platform.
**koki-dto** is a strongly-typed, immutable Kotlin DTO library centralizing all request, response, event, error,
security, and reference data contracts for the Koki platform.

[![CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml)

[![CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

The **koki-dto** module acts as the single source of truth for all data contracts across the Koki multi-tenant real
estate platform. It centralizes immutable Kotlin data classes for REST payloads (requests/responses), standardized error
envelopes, security models (JWT decoding/principal), domain events for asynchronous messaging, and reference data

## About the Project

The **koki-dto** module acts as the single source of truth for all data contracts across the Koki multi-tenant real
estate platform. It centralizes immutable Kotlin data classes for REST payloads (requests/responses), standardized error
envelopes, security models (JWT decoding/principal), domain events for asynchronous messaging, and reference data

Follow these steps to build and consume the library locally.

## About the Project

- **Java 17** installed and available on your PATH.

### 3. Clone the Monorepo

- A GitHub Personal Access Token (classic, with `read:packages`) if you plan to pull released versions from GitHub

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-dto
### 2. (Optional) Authenticate to GitHub Packages
Resulting artifact coordinates:

If you need to download dependencies or publish snapshots:
Add credentials to your `~/.m2/settings.xml` (or environment) using environment variables:
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-dto</artifactId>
    <version>VERSION_NUMBER</version>

This module is a library (no standalone runtime). There is **no application server to start**. Downstream services
include it as a dependency; no run command is required here.
Compiles sources, runs tests (if present), and installs the artifact to your local Maven repository.


### 6. Adding as a Dependency (in another module POM)


Use a placeholder version if referencing unreleased changes:


- **[CONTRIBUTING.md](../../CONTRIBUTING.md)** – Development workflow, coding standards, and pull request process.
- **[ARCHITECTURE.md](ARCHITECTURE.md)** – Detailed structure, design rationale, and security considerations.
consumed by other modules such as **koki-server**, **koki-platform**, **koki-portal**, and **koki-sdk**.
    <id>github</id>
    <username>${env.GITHUB_USER}</username>
    <password>${env.GITHUB_TOKEN}</password>
- Schema drift and duplication are eliminated across independently deployed components.

    <id>github</id>
    <username>${env.GITHUB_USER}</username>
(No secrets committed; use password-less local DBs elsewhere if needed.)

    <password>${env.GITHUB_TOKEN}</password>

- Jakarta Validation annotations on request DTOs ensure early, consistent input validation at service boundaries.
- Event DTOs enable strongly-typed message publishing/consumption in downstream services without runtime coupling.

Domain coverage includes: Accounts, Listings, Offers, Leads, Contacts, Messages, Notes, Files, Tenants, Security, Tracking, Reference Data, and Common cross-domain primitives.

This module contains no business logic, persistence code, or external network calls. It is purely a compile-time library
consumed by other modules such as **koki-server**, **koki-platform**, **koki-portal**, and **koki-sdk**.

## Getting Started

Follow these steps to set up, build, and integrate the library locally.

### 1. Prerequisites

- **Java 17** installed and on PATH
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-dto</artifactId>
    <version>VERSION_NUMBER</version>

### 2. Clone the Repository

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-dto
```

### 3. (Optional) Configure GitHub Packages Auth

See **[DEVELOP.md](../../DEVELOP.md)** for environment setup, cloning, and build workflow details.

```xml
See **[TESTING.md](../../TESTING.md)** for running unit tests, integration tests, and coverage guidance.
<id>github</id>
<username>${env.GITHUB_USER}</username>
<password>${env.GITHUB_TOKEN}</password>
    </server>
```

### 4. Build the Module

```bash
mvn clean install
```

Installs artifact to local Maven repo:

```
com.wutsi.koki:koki-dto:VERSION_NUMBER
```

### 5. Run (Not Applicable)

This module is a library only; there is **no standalone runtime process** to execute.

### 6. Add Dependency in Another Module

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-dto</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

## Contributing

General contribution guidelines are documented in **[CONTRIBUTING.md](../../CONTRIBUTING.md)**.

### Local Development

See **[DEVELOP.md](DEVELOP.md)** for module-specific build steps. For broader practices, see root **DEVELOP.md**.

### Testing

See **[TESTING.md](TESTING.md)** for module-specific notes. For broader standards, see root **TESTING.md**.

## License

Licensed under the MIT License. See **[LICENSE.md](../../LICENSE.md)** for details.
