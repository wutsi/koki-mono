# koki-dto

A Kotlin library providing shared Data Transfer Objects (DTOs), enums, and validation contracts reused across the Koki
platform APIs.

[![koki-dto CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml)

[![koki-dto CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

## About the Project

`koki-dto` centralizes all API request/response models, domain enumerations, and standardized error payloads used by
Koki services and clients. It prevents duplication and schema drift by acting as the single source of truth for public
data contracts consumed by backend services, SDKs, and user interfaces.

### Features

- **Unified Contracts** – One versioned set of DTOs shared across all Koki modules and clients.
- **Embedded Validation** – Jakarta Validation annotations (`@NotEmpty`, `@Email`, `@Size`, etc.) directly on request
  objects for automatic input checks.
- **Typed Enumerations** – Strongly-typed enums for statuses, categories, types, permissions – eliminating magic
  strings.
- **JWT Helper** – Lightweight decoder to extract principals from JWT tokens in client contexts.

## Getting Started

Add `koki-dto` as a dependency. No database, server runtime, or Spring configuration is required – this is a pure model
library.

### Prerequisites

- **Java 17+**
- **Maven 3.6+** or **Gradle 7+**
- **Kotlin 2.1.0+** (if using Kotlin)

### 1. Maven Dependency

Use `VERSION_NUMBER` as a placeholder:

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-dto</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

### 2. Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.wutsi.koki:koki-dto:VERSION_NUMBER")
}
```

### 3. GitHub Packages Repository

Maven:

```xml

<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/wutsi/koki-mono</url>
    </repository>
</repositories>
```

Gradle:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/wutsi/koki-mono")
        credentials {
            username = System.getenv("GITHUB_USER")
            password = System.getenv("GITHUB_PASSWORD")
        }
    }
}
```

### 4. Authenticate to GitHub Packages

Create a Personal Access Token with `read:packages` scope and export environment variables:

```bash
export GITHUB_USER=your-github-username
export GITHUB_PASSWORD=your-personal-access-token
```

Maven `~/.m2/settings.xml` snippet:

```xml

<settings>
    <servers>
        <server>
            <id>github</id>
            <username>${env.GITHUB_USER}</username>
            <password>${env.GITHUB_PASSWORD}</password>
        </server>
    </servers>
</settings>
```

### 5. Example Usage

Create and validate a DTO:

```kotlin
import com.wutsi.koki.account.dto.CreateAccountRequest
import jakarta.validation.Validation

val validator = Validation.buildDefaultValidatorFactory().validator
val dto = CreateAccountRequest(
    accountTypeId = 1L,
    name = "ACME Corp",
    email = "info@acme.com"
)
validator.validate(dto).forEach { v -> println("${v.propertyPath}: ${v.message}") }
```

Use enums:

```kotlin
import com.wutsi.koki.listing.dto.ListingStatus

val status = ListingStatus.PUBLISHED
```

Decode a JWT:

```kotlin
import com.wutsi.koki.security.dto.JWTDecoder

val principal = JWTDecoder().decode("your.jwt.token")
println(principal.userId)
```

## Package Overview

| Package                       | Purpose                                    |
|-------------------------------|--------------------------------------------|
| `com.wutsi.koki.account.dto`  | Accounts, attributes, summaries            |
| `com.wutsi.koki.agent.dto`    | Agent profiles & metrics                   |
| `com.wutsi.koki.contact.dto`  | Contacts & communication preferences       |
| `com.wutsi.koki.listing.dto`  | Property listings & update payloads        |
| `com.wutsi.koki.lead.dto`     | Lead creation & progression                |
| `com.wutsi.koki.offer.dto`    | Offers, versions, status transitions       |
| `com.wutsi.koki.file.dto`     | File metadata & search filters             |
| `com.wutsi.koki.message.dto`  | Internal messaging payloads                |
| `com.wutsi.koki.note.dto`     | Notes attached to entities                 |
| `com.wutsi.koki.security.dto` | Auth/JWT principal structures              |
| `com.wutsi.koki.tenant.dto`   | Tenant configuration & invitations         |
| `com.wutsi.koki.refdata.dto`  | Locations, categories, amenities           |
| `com.wutsi.koki.error.dto`    | Error codes & standardized error responses |
| `com.wutsi.koki.common.dto`   | Shared primitives & cross-cutting types    |
| `com.wutsi.koki.module.dto`   | Module and permission definitions          |
| `com.wutsi.koki.track.dto`    | Tracking & analytics events                |

## Common Patterns

Request DTO:

```kotlin
data class CreateAccountRequest(
    val accountTypeId: Long? = null,
    @get:NotEmpty @get:Size(max = 100) val name: String = "",
    @get:Email @get:Size(max = 255) val email: String = ""
)
```

Response DTO:

```kotlin
data class GetAccountResponse(val account: Account)
```

Search Response:

```kotlin
data class SearchAccountResponse(val accounts: List<AccountSummary> = emptyList())
```

ID Wrapper:

```kotlin
data class CreateAccountResponse(val id: Long)
```
