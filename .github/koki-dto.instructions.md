<!--
module: koki-dto
date: November 20, 2025
description: Shared data transfer object (DTO) library providing type-safe, validated request/response contracts for all Koki platform APIs, ensuring consistency between client and server communication.
applyTo: modules/koki-dto/**
-->

# Copilot Instructions for koki-dto

## Tech Stack

### Language

- **Kotlin 2.1.0**: Primary language for all DTO classes
- **Java 17**: Target JVM version

### Build Tools

- **Maven 3.8+**: Dependency management and build automation
- **Spring Boot Parent 3.5.7**: Parent POM for dependency version management
- Build Instructions
    - Compile the code always skip linting checks with `-Dktlint.skip=true` flag.
    - After completing all the code changes, run `ktlint -F` to fix code style issues.

### Libraries & Frameworks

- **Jakarta Validation 3.0.2**: Bean validation annotations for request validation
- **Auth0 Java-JWT 4.5.0**: JWT token encoding and decoding utilities
- **Kotlin Standard Library**: Core Kotlin functionality

### Packaging

- **JAR**: Packaged as a library for consumption by other modules
- **GitHub Packages**: Distributed via GitHub Maven repository

## Coding Style and Idioms

### File Organization

- One class/enum per file
- File name must match the class/enum name
- Package structure: `com.wutsi.koki.<domain>/dto`
- Domains: account, agent, common, contact, error, file, lead, listing, message, module, note, offer, refdata, security,
  tenant, track

### Data Classes

- **Always use `data class`** for DTOs (not regular classes)
- All properties with default values for backward compatibility
- Use immutable properties (`val`, not `var`)
- Prefer nullable types with `null` defaults over required parameters
- Order properties logically: IDs first, then business fields, then timestamps

**Example:**

```kotlin
data class Account(
    val id: Long = -1,
    val accountTypeId: Long? = null,
    val name: String = "",
    val phone: String? = null,
    val email: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
```

### Validation Annotations

- Use Jakarta Validation annotations on request DTOs only (not on entity/response DTOs)
- Apply annotations using `@get:` prefix for property getters
- Common annotations: `@NotEmpty`, `@Size`, `@Email`, `@Min`, `@Max`

**Example:**

```kotlin
data class CreateAccountRequest(
    @get:NotEmpty @get:Size(max = 100) val name: String = "",
    @get:NotEmpty @get:Email @get:Size(max = 255) val email: String = "",
    @get:Size(max = 30) val phone: String? = null,
)
```

### Enums

- Use `enum class` for all enumerations
- Always include `UNKNOWN` as first value for default/unrecognized states
- Use UPPER_SNAKE_CASE for enum constants
- No companion objects or methods unless required (e.g., ErrorCode)

**Example:**

```kotlin
enum class ListingStatus {
    UNKNOWN,
    DRAFT,
    ACTIVE,
    PENDING,
    SOLD,
}
```

### Object Constants

- Use `object` for constant collections (e.g., ErrorCode)
- Define constants as public properties
- Use consistent naming patterns with prefix (e.g., `$PREFIX:account:not-found`)

**Example:**

```kotlin
object ErrorCode {
    private val PREFIX = "urn:wutsi:koki:error"
    val ACCOUNT_NOT_FOUND: String = "$PREFIX:account:not-found"
}
```

### Naming Conventions

#### Request DTOs

- Pattern: `<Verb><Entity>Request`
- Examples: `CreateAccountRequest`, `UpdateListingRequest`, `SearchLeadRequest`

#### Response DTOs

- Pattern: `<Verb><Entity>Response` or `Get<Entity>Response`
- Examples: `CreateAccountResponse`, `GetAccountResponse`, `SearchAccountResponse`

#### Entity DTOs

- Use singular entity name (e.g., `Account`, `Listing`, `Lead`)
- Full entity with all fields

#### Summary DTOs

- Pattern: `<Entity>Summary`
- Examples: `AccountSummary`, `ListingSummary`
- Lightweight version with essential fields only (typically ID, name, key fields, timestamps)

#### Event DTOs

- Package: `<domain>/dto/event`
- Pattern: `<Entity><Event>Event`
- Examples: `LeadCreatedEvent`, `ListingStatusChangedEvent`

### Types

#### Dates

- Use `java.util.Date` for all date/time fields
- Default value: `Date()` (current time)

#### Money

- Use `com.wutsi.koki.refdata.dto.Money` data class
- Fields: `amount: Double`, `currency: String`

#### Address

- Use `com.wutsi.koki.refdata.dto.Address` data class
- Fields: street, city, zip, country, etc.

#### GeoLocation

- Use `com.wutsi.koki.refdata.dto.GeoLocation` data class
- Fields: `latitude: Double`, `longitude: Double`

#### Collections

- Use immutable collections: `List`, `Map`, `Set`
- Default to empty collections: `emptyList()`, `emptyMap()`, `emptySet()`
- Never use `ArrayList`, `HashMap`, etc. in signatures

#### IDs

- Use `Long` for all entity IDs
- Default value: `-1` for entity IDs (indicates not set)
- Use `Long?` (nullable) for optional foreign keys with `null` default

### Imports

- No wildcard imports
- Import only what's needed
- Standard order: Java, Jakarta, Kotlin, com.wutsi.koki

## Architecture

### Package Structure

```
com.wutsi.koki/
├── account/dto/          # Account management (12 files)
├── agent/dto/            # AI agent integration (7 files)
├── common/dto/           # Shared cross-cutting DTOs (5 files)
├── contact/dto/          # Contact management (9 files)
├── error/dto/            # Error handling (5 files)
├── file/dto/             # File management (10 files)
├── lead/dto/             # Lead tracking (11 files)
├── listing/dto/          # Property listings (25 files)
│   └── event/           # Listing events
├── message/dto/          # Messaging (9 files)
│   └── event/           # Message events
├── module/dto/           # Module management (5 files)
├── note/dto/             # Notes (11 files)
├── offer/dto/            # Offer management (18 files)
├── refdata/dto/          # Reference data (15 files)
├── security/dto/         # Security & JWT (5 files)
├── tenant/dto/           # Multi-tenancy (43 files)
└── track/dto/            # Analytics tracking (5 files)
```

### Domain Patterns

#### CRUD Operations

Each domain typically includes:

- **Entity**: Full object representation (e.g., `Account`)
- **Summary**: Lightweight list item (e.g., `AccountSummary`)
- **CreateRequest/Response**: Creation endpoints
- **UpdateRequest**: Update endpoints (may have multiple for different update operations)
- **GetResponse**: Single entity retrieval wrapper
- **SearchResponse**: List retrieval with total count

#### Search Response Pattern

```kotlin
data class SearchXResponse(
    val total: Long = -1L,
    val items: List<XSummary> = emptyList()
)
```

#### Get Response Pattern

```kotlin
data class GetXResponse(
    val entity: X = X()
)
```

#### Create Response Pattern

```kotlin
data class CreateXResponse(
    val id: Long = -1
)
```

### Special Components

#### Security (JWT)

- `JWTDecoder`: Decodes and validates JWT tokens
- `JWTPrincipal`: Represents authenticated user with claims
- `JWTEncoder`: Not implemented (uses default Algorithm.none())

#### Error Handling

- `ErrorResponse`: Top-level error wrapper
- `Error`: Error details with code, message, traceId
- `Parameter`: Field-level validation errors
- `ErrorCode`: Centralized error code constants

#### Reference Data

- `Address`: Physical addresses
- `GeoLocation`: Latitude/longitude coordinates
- `Money`: Amount with currency
- Common enums: `IDType`, `LocationType`, `CategoryType`

## Testing Guidelines

### Current State

- **No tests**: This is a pure DTO library with no business logic
- DTOs are validated through:
    - Compilation (Kotlin type safety)
    - Jakarta Validation annotations (runtime validation in consuming services)
    - Integration tests in consuming modules (koki-server, koki-portal)

### When Tests Would Be Required

- If utility classes with logic are added (like JWTDecoder/JWTEncoder)
- If custom validation annotations are implemented
- If enum methods or companion object functions are added

### Test Framework (if needed)

- JUnit 5
- Kotlin test utilities
- Mockito for mocking

## Documentation Guidelines

### Class Documentation

- Not required for simple DTOs (self-documenting with clear names)
- Document complex business rules or validation requirements as comments when needed
- Document deprecated features with `@Deprecated` annotation and reason

### README.md

- Must document:
    - Purpose and scope
    - Complete list of features (domains covered)
    - Tech stack with badges
    - Architecture overview with package structure
    - Domain reference with descriptions
    - Usage examples for common patterns
    - License information

### Package Documentation

- Not used in this module
- Domain organization is self-evident from package names

### Field Documentation

- Not required for standard fields
- Add inline comments for non-obvious business rules
- Document validation constraints if complex

## Behavior

### Backward Compatibility

- **Never remove fields** from existing DTOs (mark as deprecated instead)
- **Never change field types** (add new field if type change needed)
- **Always provide default values** for new fields
- Use nullable types for optional fields added later

### Immutability

- All DTOs are immutable by design (data classes with `val` properties)
- Use `copy()` method for creating modified versions

### Serialization

- DTOs serialize to JSON automatically via Jackson (configured in consuming services)
- Field names match JSON property names (no @JsonProperty needed)
- Null fields are typically omitted in JSON (configured in consuming services)
- Date fields serialize as timestamps

### Validation

- Request DTOs include Jakarta Validation annotations
- Validation executed in consuming services (not in DTO module)
- Entity and Response DTOs have no validation annotations

### Evolution Pattern

- Add new optional fields with default values
- Create new request DTOs for new operations (don't modify existing)
- Version in POM follows semantic versioning
- Breaking changes require major version bump

### Common Patterns

#### Multiple Update Requests

Some entities have multiple update requests for different concerns:

- `UpdateListingRequest`: General listing updates
- `UpdateListingPriceRequest`: Price-specific updates
- `UpdateListingAddressRequest`: Address-specific updates
- `UpdateListingAmenitiesRequest`: Amenities-specific updates

This pattern allows fine-grained permissions and validation.

#### Status Change Requests

```kotlin
data class UpdateXStatusRequest(
    val status: XStatus,
    val reason: String? = null,
)
```

#### Import/Export

- `ImportResponse`: Standard response for bulk imports with success count and errors
- `ImportMessage`: Individual error message from import operation

### Dependencies

- **Minimal external dependencies**: Only Jakarta Validation and Java-JWT
- **No Spring dependencies**: This is a standalone library
- **No database dependencies**: Pure data structures
- **No Jackson annotations**: Serialization configured in consuming services

### Build Behavior

- Built with Maven: `mvn clean install`
- Deployed to GitHub Packages
- No code formatting enforcement (handled by ktlint in parent)
- No code coverage requirements (no tests)

