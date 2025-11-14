# koki-dto

A Kotlin library providing shared Data Transfer Objects (DTOs), enumerations, and validation contracts for the Koki
platform APIs.

[![koki-dto CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml)

[![koki-dto CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

## About the Project

The **koki-dto** module serves as the single source of truth for all API contracts across the Koki platform. It
centralizes request/response models, domain enumerations, validation rules, and event payloads to ensure type safety,
prevent schema drift, and maintain consistency across backend services, SDKs, and frontend applications. By providing a
pure library artifact with no business logic or runtime dependencies, it enables independent versioning and reduces
coupling between services.

### Features

- **Unified Contracts**: One versioned set of DTOs shared across all Koki modules and clients, preventing duplication
  and schema drift
- **Embedded Validation**: Jakarta Validation annotations (`@NotEmpty`, `@Email`, `@Size`, `@Valid`) directly on request
  objects for automatic server-side input validation
- **Strongly-Typed Enumerations**: Kotlin enums for statuses, types, and categories eliminating magic strings and
  enabling compile-time validation
- **Event-Driven Support**: Domain event payloads for asynchronous messaging, event sourcing, and microservices
  integration
- **JWT Utilities**: Lightweight token decoder for extracting authentication principals from JWT tokens in client
  contexts

## Getting Started

Add **koki-dto** as a dependency to your project. No database, server runtime, or Spring configuration is required—this
is a pure data contract library.

### Prerequisites

- **Java 17+**
- **Maven 3.6+** or **Gradle 7+**
- **Kotlin 2.1.0+** (if using Kotlin)

### 1. Add Maven Dependency

Add the following dependency to your **pom.xml**:

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-dto</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

### 2. Add Gradle Dependency (Kotlin DSL)

Add the following to your **build.gradle.kts**:

```kotlin
dependencies {
    implementation("com.wutsi.koki:koki-dto:VERSION_NUMBER")
}
```

### 3. Configure GitHub Packages Repository

The **koki-dto** artifact is published to GitHub Packages. Configure your build tool to access it.

**Maven** - Add to **pom.xml**:

```xml

<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/wutsi/koki-mono</url>
    </repository>
</repositories>
```

**Gradle** - Add to **build.gradle.kts**:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/wutsi/koki-mono")
        credentials {
            username = System.getenv("GITHUB_USER")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}
```

### 4. Authenticate to GitHub Packages

Create a Personal Access Token with **read:packages** scope from your GitHub account settings.

Export environment variables:

```bash
export GITHUB_USER=your-github-username
export GITHUB_TOKEN=your-personal-access-token
```

For Maven, add credentials to **~/.m2/settings.xml**:

```xml

<settings>
    <servers>
        <server>
            <id>github</id>
            <username>${env.GITHUB_USER}</username>
            <password>${env.GITHUB_TOKEN}</password>
        </server>
    </servers>
</settings>
```

### 5. Using DTOs in Your Code

**Request DTOs with Validation**:

```kotlin
import com.wutsi.koki.account.dto.CreateAccountRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController {

    @PostMapping
    fun createAccount(@Valid @RequestBody request: CreateAccountRequest): CreateAccountResponse {
        // Validation is automatically triggered by @Valid annotation
        // All constraints on CreateAccountRequest are checked before reaching this code
        return accountService.createAccount(request)
    }
}
```

**Response DTOs**:

```kotlin
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.Account

@GetMapping("/{id}")
fun getAccount(@PathVariable id: Long): GetAccountResponse {
    val account = accountService.findById(id)
    return GetAccountResponse(account = account)
}
```

**Enumerations**:

```kotlin
import com.wutsi.koki.lead.dto.LeadStatus
import com.wutsi.koki.listing.dto.PropertyType

val lead = Lead(
    status = LeadStatus.QUALIFIED,
    // ...
)

val listing = Listing(
    propertyType = PropertyType.APARTMENT,
    // ...
)
```

**Event Publishing**:

```kotlin
import com.wutsi.koki.tenant.dto.event.UserCreatedEvent

fun publishUserCreatedEvent(userId: Long, tenantId: Long) {
    val event = UserCreatedEvent(
        userId = userId,
        tenantId = tenantId,
        timestamp = System.currentTimeMillis()
    )
    eventPublisher.publish(event)
}
```

**JWT Token Decoding**:

```kotlin
import com.wutsi.koki.security.dto.JWTDecoder

val decoder = JWTDecoder()
val principal = decoder.decode(accessToken)
println("User ID: ${principal.userId}")
println("Tenant ID: ${principal.tenantId}")
```

### 6. Build the Module

Clone the repository and build the module:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-dto
mvn clean install
```

### 7. Run Tests

Execute unit tests:

```bash
mvn test
```

Generate code coverage report:

```bash
mvn clean test jacoco:report
```

The coverage report will be available at **target/site/jacoco/index.html**.

### 8. Code Style

The project uses **ktlint** for Kotlin code style enforcement. Run the linter:

```bash
mvn antrun:run@ktlint
```

Auto-format code:

```bash
mvn antrun:run@ktlint-format
```

