# GitHub Copilot Instructions for koki-sdk Module

You are an expert Kotlin/Java developer specializing in REST API client development, SDK design, and Spring Framework
integration.
Your primary goal is to assist with the **koki-sdk** module - a type-safe Kotlin client library for seamless integration
with Koki REST APIs.

## Apply To

- `modules/koki-sdk/**`

---

## Project Context

### Module Purpose

The **koki-sdk** module provides:

- Type-safe Kotlin client classes for all Koki Server REST API endpoints
- Domain-focused API wrappers organized by business capabilities
- Simplified HTTP communication using Spring's `RestTemplate`
- Automatic URL construction with query parameter encoding
- Multi-tenant support with automatic context propagation
- File upload capabilities for multipart form data
- Consistent error handling using server-side DTOs

### Technology Stack

- **Language**: Kotlin 1.9.25
- **Build Tool**: Maven
- **Java Version**: 17
- **Frameworks**: Spring Framework 6.x, Spring Boot 3.5.7
- **Dependencies**:
    - `koki-dto`: Shared DTO contracts for request/response models
    - `koki-platform`: Platform utilities (TenantProvider, AccessTokenHolder)
    - `commons-io`: File I/O utilities
    - `spring-web`: RestTemplate and HTTP support

### Architecture Principles

1. **Type Safety**: Leverage Kotlin's type system for compile-time guarantees
2. **Immutability**: Use `val` properties and immutable data structures
3. **Domain Separation**: Organize API clients by business domain
4. **Shared Contracts**: Use DTOs from `koki-dto` module (never duplicate)
5. **Dependency Injection**: Design for Spring's DI container
6. **Simplicity**: Keep client code thin - delegate to server for business logic

---

## Code Generation Guidelines

### 1. API Client Classes

When generating or modifying API client classes:

#### Structure

```kotlin
class KokiDomainName(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/resource-name"
    }

    // Methods...
}
```

#### Method Patterns

- **Create Resource**: Returns `Create{Resource}Response` with the new resource ID

```kotlin
fun create(request: Create {
    Resource
}Request): Create { Resource } Response {
    val url = urlBuilder.build(PATH_PREFIX)
    return rest.postForEntity(url, request, Create { Resource } Response ::class.java).body
}
```

- **Update Resource**: Returns `Unit` (void), uses POST to resource ID endpoint

```kotlin
fun update(id: Long, request: Update {
    Resource
}Request) {
    val url = urlBuilder.build("$PATH_PREFIX/$id")
    rest.postForEntity(url, request, Any::class.java)
}
```

- **Delete Resource**: Returns `Unit`, uses DELETE method

```kotlin
fun delete(id: Long) {
    val url = urlBuilder.build("$PATH_PREFIX/$id")
    rest.delete(url)
}
```

- **Get Single Resource**: Returns `Get{Resource}Response` with full resource details

```kotlin
fun get(id: Long): Get {
    Resource
}Response {
    val url = urlBuilder.build("$PATH_PREFIX/$id")
    return rest.getForEntity(url, Get { Resource } Response ::class.java).body
}
```

- **Search/List Resources**: Returns `Search{Resource}Response`, supports pagination and filters

```kotlin
fun search(
    ids: List<Long> = emptyList(),
    keyword: String? = null,
    status: {
    Resource
}Status? = null,
limit: Int = 20,
offset: Int = 0,
): Search { Resource } Response {
    val url = urlBuilder.build(
        PATH_PREFIX,
        mapOf(
            "id" to ids,
            "q" to keyword,
            "status" to status,
            "limit" to limit,
            "offset" to offset,
        )
    )
    return rest.getForEntity(url, Search { Resource } Response ::class.java).body
}
```

- **Upload File**: Extends `AbstractKokiModule`, uses `upload()` helper method

```kotlin
fun upload(file: MultipartFile): UploadResponse {
    val url = urlBuilder.build("$PATH_PREFIX/upload")
    return upload(url, file, UploadResponse::class.java)
}
```

#### Naming Conventions

- **Class Name**: `Koki{DomainName}` (e.g., `KokiAccounts`, `KokiListings`, `KokiLead`)
- **Method Name**: Use descriptive verb + noun (e.g., `create`, `update`, `search`, `get`, `delete`)
- **Path Prefix**: Use lowercase with hyphens (e.g., `/v1/accounts`, `/v1/listings`)

#### Best Practices

- Always use `private val` for constructor parameters
- Use `companion object` for constants (PATH_PREFIX, etc.)
- Extend `AbstractKokiModule` only if the client needs file upload support
- Never use `!!` (non-null assertion) - use `.body` without assertion
- Use `emptyList()` or `emptyMap()` as default parameter values for collections
- Group related parameters in search methods
- Always include `limit` and `offset` parameters for pagination

---

### 2. URL Building

The `URLBuilder` class handles URL construction with query parameters:

#### Usage

```kotlin
// Simple path
val url = urlBuilder.build("/v1/accounts")

// Path with parameters
val url = urlBuilder.build(
    "/v1/accounts",
    mapOf(
        "q" to keyword,              // Single nullable value
        "id" to ids,                 // Collection
        "status" to status,          // Enum
        "limit" to 20,               // Primitive
        "offset" to 0,
    )
)
```

#### Rules

- Null values are automatically filtered out
- Empty collections are filtered out
- Collections are serialized as repeated query parameters (e.g., `?id=1&id=2&id=3`)
- Enum values are serialized using their name
- All values are URL-encoded automatically

---

### 3. Request/Response DTOs

**IMPORTANT**: Never create DTOs in the koki-sdk module. Always use DTOs from the `koki-dto` module.

#### DTO Organization (in koki-dto)

DTOs are organized by domain package:

- `com.wutsi.koki.account.dto.*` - Account-related DTOs
- `com.wutsi.koki.listing.dto.*` - Listing-related DTOs
- `com.wutsi.koki.lead.dto.*` - Lead-related DTOs
- `com.wutsi.koki.contact.dto.*` - Contact-related DTOs
- `com.wutsi.koki.file.dto.*` - File-related DTOs
- `com.wutsi.koki.message.dto.*` - Message-related DTOs
- `com.wutsi.koki.note.dto.*` - Note-related DTOs
- `com.wutsi.koki.refdata.dto.*` - Reference data DTOs
- `com.wutsi.koki.tenant.dto.*` - Tenant and user DTOs
- `com.wutsi.koki.security.dto.*` - Authentication DTOs
- `com.wutsi.koki.common.dto.*` - Common/shared DTOs

#### DTO Naming Patterns

- **Request DTOs**: `Create{Resource}Request`, `Update{Resource}Request`, `Search{Resource}Request`
- **Response DTOs**: `Create{Resource}Response`, `Get{Resource}Response`, `Search{Resource}Response`
- **Domain Models**: `{Resource}` (e.g., `Lead`, `Account`, `Listing`)
- **Summary Models**: `{Resource}Summary` (lighter version for lists)
- **Enums**: `{Resource}Status`, `{Resource}Type`, `{Resource}Sort`

#### DTO Structure Example

```kotlin
// Request DTO
data class CreateLeadRequest(
    val listingId: Long = -1,
    @get:NotEmpty @get:Size(max = 50) val firstName: String = "",
    @get:NotEmpty @get:Size(max = 50) val lastName: String = "",
    @get:NotEmpty @get:Size(max = 100) val email: String = "",
    val message: String? = null,
    val source: LeadSource = LeadSource.UNKNOWN,
)

// Response DTO
data class CreateLeadResponse(
    val id: Long = -1,
)

// Domain Model
data class Lead(
    val id: Long = -1,
    val listingId: Long? = null,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val firstName: String = "",
    val lastName: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
```

---

### 4. Multi-Tenant Support

The SDK is designed for multi-tenant environments:

#### Tenant Context

- `TenantProvider` provides the current tenant ID
- Tenant ID is automatically added to requests via Spring interceptors
- No need to manually add tenant headers in SDK code

#### Access Token

- `AccessTokenHolder` provides the current JWT access token
- Access token is automatically added to requests via Spring interceptors
- For file upload URLs, manually include: `"access-token" to accessTokenHolder.get()`

---

### 5. File Upload Support

For API clients that need file upload capabilities:

#### Extend AbstractKokiModule

```kotlin
class KokiFiles(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
    private val tenantProvider: TenantProvider,
    private val accessTokenHolder: AccessTokenHolder,
) : AbstractKokiModule(rest) {
    // Methods...
}
```

#### Upload Method Pattern

```kotlin
fun upload(
    ownerId: Long?,
    ownerType: ObjectType?,
    type: FileType,
    file: MultipartFile
): UploadFileResponse {
    val url = uploadUrl(ownerId, ownerType, type)
    return upload(url, file, UploadFileResponse::class.java)
}

fun uploadUrl(
    ownerId: Long?,
    ownerType: ObjectType?,
    type: FileType,
): String {
    return urlBuilder.build(
        "$PATH_PREFIX/upload",
        mapOf(
            "owner-id" to ownerId,
            "owner-type" to ownerType,
            "type" to type,
            "tenant-id" to tenantProvider.id(),
            "access-token" to accessTokenHolder.get()
        )
    )
}
```

#### Notes

- The `upload()` method is inherited from `AbstractKokiModule`
- Provide separate `uploadUrl()` method for frontend direct upload scenarios
- Always include tenant-id and access-token in upload URLs

---

### 6. Error Handling

The SDK relies on Spring's `RestTemplate` for error handling:

#### Exceptions

- `RestTemplate` automatically throws `HttpClientErrorException` for 4xx errors
- `RestTemplate` automatically throws `HttpServerErrorException` for 5xx errors
- Application code should catch and handle these exceptions as needed

#### Response Validation

- Use `.body` without null assertion for response extraction
- Assume non-null responses from successful REST calls
- Let Spring handle null body scenarios with appropriate exceptions

---

### 7. Pagination Support

All search/list operations should support pagination:

#### Standard Parameters

```kotlin
fun search(
    // ... filter parameters ...
    limit: Int = 20,    // Default page size
    offset: Int = 0,    // Default starting position
): SearchResponse {
    val url = urlBuilder.build(
        PATH_PREFIX,
        mapOf(
            // ... filters ...
            "limit" to limit,
            "offset" to offset,
        )
    )
    return rest.getForEntity(url, SearchResponse::class.java).body
}
```

#### Best Practices

- Always provide sensible defaults (limit=20, offset=0)
- Use `Int` type for pagination parameters
- Document pagination behavior in method KDoc

---

### 8. Documentation Standards

#### KDoc Comments

Add KDoc to all public methods:

```kotlin
/**
 * Creates a new lead in the system.
 *
 * @param request The lead creation request containing contact details
 * @return Response containing the ID of the created lead
 */
fun create(request: CreateLeadRequest): CreateLeadResponse {
    // Implementation...
}

/**
 * Searches for leads matching the specified criteria.
 *
 * @param ids Filter by specific lead IDs (optional)
 * @param keyword Search keyword for name, email, or phone (optional)
 * @param statuses Filter by lead statuses (optional)
 * @param limit Maximum number of results to return (default: 20)
 * @param offset Number of results to skip for pagination (default: 0)
 * @return Search response containing matching leads and total count
 */
fun search(
    ids: List<Long> = emptyList(),
    keyword: String? = null,
    statuses: List<LeadStatus> = emptyList(),
    limit: Int = 20,
    offset: Int = 0,
): SearchLeadResponse {
    // Implementation...
}
```

#### Class-Level Documentation

```kotlin
/**
 * Client for managing leads in the Koki system.
 *
 * Provides operations for creating, updating, and searching leads,
 * as well as managing lead status transitions.
 */
class KokiLead(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    // Implementation...
}
```

---

### 9. Testing Considerations

When writing tests for SDK clients:

#### Unit Tests

- Mock `RestTemplate` to verify URL construction and parameter passing
- Test query parameter encoding for special characters
- Verify collection parameter serialization
- Test null parameter filtering

#### Integration Tests

- Not typically included in SDK module (tested via server integration tests)
- If needed, use WireMock or similar for HTTP mocking

---

## Common Patterns and Examples

### Pattern 1: Simple CRUD Client

```kotlin
class KokiNotes(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/notes"
    }

    fun create(request: CreateNoteRequest): CreateNoteResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateNoteResponse::class.java).body
    }

    fun update(id: Long, request: UpdateNoteRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, request, Any::class.java)
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }

    fun get(id: Long): GetNoteResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetNoteResponse::class.java).body
    }

    fun search(
        ids: List<Long> = emptyList(),
        ownerId: Long? = null,
        ownerType: ObjectType? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchNoteResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "owner-id" to ownerId,
                "owner-type" to ownerType,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchNoteResponse::class.java).body
    }
}
```

### Pattern 2: Client with State Transitions

```kotlin
class KokiListings(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/listings"
    }

    fun publish(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/publish")
        rest.postForEntity(url, emptyMap<String, Any>(), Any::class.java)
    }

    fun close(id: Long, request: CloseListingRequest) {
        val url = urlBuilder.build("$PATH_PREFIX/$id/close")
        rest.postForEntity(url, request, Any::class.java)
    }
}
```

### Pattern 3: Client with Sub-Resources

```kotlin
class KokiAccounts(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val ACCOUNT_PATH_PREFIX = "/v1/accounts"
        private const val ATTRIBUTE_PATH_PREFIX = "/v1/attributes"
    }

    fun account(id: Long): GetAccountResponse {
        val url = urlBuilder.build("$ACCOUNT_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetAccountResponse::class.java).body
    }

    fun attribute(id: Long): GetAttributeResponse {
        val url = urlBuilder.build("$ATTRIBUTE_PATH_PREFIX/$id")
        return rest.getForEntity(url, GetAttributeResponse::class.java).body
    }

    fun uploadAttributes(file: MultipartFile): ImportResponse {
        val url = urlBuilder.build("$ATTRIBUTE_PATH_PREFIX/csv")
        return upload(url, file, ImportResponse::class.java)
    }
}
```

### Pattern 4: Authentication Client (No Tenant Context)

```kotlin
class KokiAuthentication(
    private val urlBuilder: URLBuilder,
    private val rest: RestTemplate,
) {
    companion object {
        private const val PATH_PREFIX = "/v1/auth"
    }

    fun login(request: LoginRequest): LoginResponse {
        val url = urlBuilder.build("$PATH_PREFIX/login")
        return rest.postForEntity(url, request, LoginResponse::class.java).body
    }
}
```

---

## Domain Organization

The SDK is organized by business domains:

| Client Class         | Domain        | Purpose                           |
|----------------------|---------------|-----------------------------------|
| `KokiAuthentication` | Security      | User authentication and login     |
| `KokiAccounts`       | CRM           | Account management and attributes |
| `KokiContacts`       | CRM           | Contact management                |
| `KokiListings`       | Real Estate   | Property listing management       |
| `KokiLead`           | Sales         | Lead tracking and conversion      |
| `KokiOffer`          | Sales         | Offer management                  |
| `KokiOfferVersion`   | Sales         | Offer version control             |
| `KokiFiles`          | Storage       | File upload and management        |
| `KokiMessages`       | Communication | Message sending and tracking      |
| `KokiNotes`          | Collaboration | Note creation and management      |
| `KokiRefData`        | Reference     | Locations, categories, amenities  |
| `KokiUsers`          | IAM           | User management                   |
| `KokiRoles`          | IAM           | Role and permission management    |
| `KokiTenants`        | Multi-Tenancy | Tenant management                 |
| `KokiConfiguration`  | Settings      | Tenant configuration              |
| `KokiModules`        | System        | Module/feature management         |
| `KokiInvitations`    | Onboarding    | User invitation management        |
| `KokiTypes`          | Metadata      | Type system management            |
| `KokiAgent`          | Automation    | AI agent operations               |

---

## Code Style Guidelines

### Kotlin Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `ktlint` for automatic formatting (configured in pom.xml)
- Prefer expression bodies for simple functions
- Use trailing commas in parameter lists and map entries

### Naming

- **Classes**: PascalCase with `Koki` prefix (e.g., `KokiListings`)
- **Methods**: camelCase, descriptive verbs (e.g., `create`, `search`, `updateStatus`)
- **Properties**: camelCase (e.g., `urlBuilder`, `rest`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `PATH_PREFIX`)
- **Parameters**: camelCase (e.g., `keyword`, `ownerId`)

### Formatting

- Indent with 4 spaces
- Maximum line length: 120 characters
- Use blank lines to separate logical sections
- Group imports by category (stdlib, third-party, project)

---

## Important Reminders

1. **Never create DTOs in koki-sdk** - Always import from `koki-dto` module
2. **No business logic** - Keep SDK clients thin and focused on HTTP communication
3. **Type safety** - Leverage Kotlin's type system, avoid `!!` and `Any`
4. **Immutability** - Use `val` for properties, prefer immutable collections
5. **Dependency injection** - Design for Spring's DI container
6. **Consistent patterns** - Follow established patterns for CRUD operations
7. **Documentation** - Add KDoc to all public APIs
8. **Testing** - Mock `RestTemplate`, verify URL construction
9. **Multi-tenant** - Respect tenant context in all operations
10. **Pagination** - Always support limit/offset for search operations

---

## Related Modules

- **koki-dto**: Shared DTOs and contracts (source of truth for data structures)
- **koki-platform**: Platform utilities (TenantProvider, AccessTokenHolder, etc.)
- **koki-server**: REST API server implementation (SDK consumer)

---

## Additional Context

### Why This Module Exists

- Provides type-safe, idiomatic Kotlin API for Koki Server
- Eliminates boilerplate HTTP client code in consuming applications
- Centralizes URL construction and parameter encoding logic
- Enables compile-time validation of API contracts
- Simplifies testing via Spring's RestTemplate mocking

### When to Modify This Module

- Adding new REST API endpoints to koki-server
- Adding new query parameters to existing endpoints
- Changing request/response contracts (coordinate with koki-dto)
- Adding new domain-specific API clients
- Improving error handling or retry logic

### When NOT to Modify This Module

- Adding business logic (belongs in koki-server)
- Creating new DTOs (belongs in koki-dto)
- Adding validation logic (belongs in koki-server or koki-dto)
- Implementing caching or rate limiting (belongs in consumer applications)

