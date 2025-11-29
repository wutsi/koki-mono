<!--
module: koki-sdk
date: November 20, 2025
description: Type-safe Kotlin client library for seamless integration with Koki REST APIs, providing domain-focused wrappers for authentication, accounts, listings, offers, files, and tenant management.
applyTo: modules/koki-sdk/**
-->

# Copilot Instructions for koki-sdk

## Tech Stack

### Language

- **Kotlin 2.1.0**: Primary language for all SDK client classes
- **Java 17**: Target JVM version

### Build Tools

- **Maven 3.8+**: Dependency management and build automation
- **Spring Boot Parent 3.5.7**: Parent POM for dependency version management

### Frameworks & Libraries

- **Spring Web 6.x**: RestTemplate for HTTP communication (provided scope)
- **Spring Boot 3.5.7**: Configuration and dependency injection support (provided scope)
- **koki-dto**: Shared DTOs for request/response contracts
- **koki-platform**: Infrastructure support (TenantProvider, AccessTokenHolder)
- **Apache Commons IO**: File utilities for multipart upload handling

### Packaging

- **JAR**: Packaged as a library for consumption by client applications
- **GitHub Packages**: Distributed via GitHub Maven repository

## Coding Style and Idioms

### File Organization

- One class per file
- File name must match the class name
- Package structure: `com.wutsi.koki.sdk`
- All SDK client classes in root `sdk` package
- No subpackages or domain-specific packages

### Class Structure

#### SDK Client Classes

- Pattern: `Koki<Domain>` (e.g., `KokiAccounts`, `KokiListings`, `KokiFiles`)
- All SDK clients are regular classes (not data classes)
- Extend `AbstractKokiModule` if file upload functionality is needed
- Otherwise, standalone classes with constructor injection

**Example:**

```kotlin
class KokiAccounts(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/accounts"
    }

    fun create(request: CreateAccountRequest): CreateAccountResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreateAccountResponse::class.java).body
    }
}
```

#### Base Classes

- **AbstractKokiModule**: Base class for SDK clients requiring file upload
    - Constructor parameter: `protected val rest: RestTemplate`
    - Provides `upload()` method for multipart file uploads
    - Uses Apache Commons IO and Spring MultipartFile

- **URLBuilder**: URL construction utility
    - Constructor parameter: `private val baseUrl: String`
    - Method: `build(path: String, parameters: Map<String, Any?> = emptyMap()): String`
    - Handles query parameter encoding, collection serialization, null filtering

### Naming Conventions

#### SDK Client Classes

- Pattern: `Koki<Domain>`
- Examples: `KokiAuthentication`, `KokiAccounts`, `KokiListings`, `KokiOffer`
- Use singular for domain name (e.g., `KokiOffer`, not `KokiOffers`, unless it's the actual domain name)

#### Methods

- Use simple, clear method names based on operations:
    - **Create**: `create(request)` → returns `Create<Entity>Response`
    - **Read single**: `entity(id)` or domain-specific name (e.g., `account(id)`, `listing(id)`)
    - **Read single with wrapper**: `get(id)` → returns `Get<Entity>Response`
    - **Update**: `update(id, request)` → returns `Unit` or no return
    - **Delete**: `delete(id)` → returns `Unit`
    - **Search**: `entities(...)` with search parameters (e.g., `accounts(...)`, `listings(...)`)
    - **Specialized updates**: `update<Aspect>(id, request)` (e.g., `updateAmenities()`, `updateAddress()`)

#### Path Constants

- Use companion object for path constants
- Pattern: `private const val PATH_PREFIX = "/v1/<domain>"`
- Use `PATH_PREFIX` consistently in all methods

### Method Patterns

#### Create Operations

```kotlin
fun create(request: CreateXRequest): CreateXResponse {
    val url = urlBuilder.build(PATH_PREFIX)
    return rest.postForEntity(url, request, CreateXResponse::class.java).body
}
```

#### Read Single Operations

```kotlin
fun entity(id: Long): GetEntityResponse {
    val url = urlBuilder.build("$PATH_PREFIX/$id")
    return rest.getForEntity(url, GetEntityResponse::class.java).body
}
```

#### Update Operations

```kotlin
fun update(id: Long, request: UpdateXRequest) {
    val url = urlBuilder.build("$PATH_PREFIX/$id")
    rest.postForEntity(url, request, Any::class.java)
}
```

#### Delete Operations

```kotlin
fun delete(id: Long) {
    val url = urlBuilder.build("$PATH_PREFIX/$id")
    rest.delete(url)
}
```

#### Search Operations

```kotlin
fun entities(
    keyword: String?,
    ids: List<Long>,
    categoryIds: List<Long>,
    status: EntityStatus?,
    limit: Int,
    offset: Int,
): SearchEntityResponse {
    val url = urlBuilder.build(
        PATH_PREFIX,
        mapOf(
            "q" to keyword,
            "id" to ids,
            "category-id" to categoryIds,
            "status" to status,
            "limit" to limit,
            "offset" to offset,
        )
    )
    return rest.getForEntity(url, SearchEntityResponse::class.java).body
}
```

#### File Upload Operations

```kotlin
fun uploadEntity(file: MultipartFile): ImportResponse {
    val url = urlBuilder.build("$PATH_PREFIX/csv")
    return upload(url, file, ImportResponse::class.java)
}
```

### Parameter Handling

#### Query Parameters

- Use `urlBuilder.build()` with parameter map for all query parameters
- Use kebab-case for parameter names: `"account-type-id"`, `"managed-by-id"`
- Pass nullable parameters and empty collections directly - URLBuilder handles filtering
- Always include pagination: `limit` and `offset`

#### Collections

- Use `List<Long>` for ID collections (not varargs)
- Use `List<String>` for string collections (e.g., permissions)
- Default to `emptyList()` for optional collection parameters
- URLBuilder automatically serializes collections as repeated parameters: `?id=1&id=2&id=3`

#### Nullable Parameters

- Use `?` for optional parameters
- Default to `null` for optional primitive types
- URLBuilder filters out null values automatically

### HTTP Operations

#### Using RestTemplate

- **POST for create**: `rest.postForEntity(url, request, ResponseType::class.java).body`
- **POST for update**: `rest.postForEntity(url, request, Any::class.java)` - no return value
- **GET**: `rest.getForEntity(url, ResponseType::class.java).body`
- **DELETE**: `rest.delete(url)` - no return value

#### Return Values

- Always return response body directly (no wrapping in Optional or Result)
- Use `!!` assertion for responses that must exist (from GET operations)
- Omit return type for `Unit` operations (updates, deletes)

### File Upload Handling

#### For SDK Clients with Upload

```kotlin
class KokiFiles(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
    private val tenantProvider: TenantProvider,
    private val accessTokenHolder: AccessTokenHolder,
) : AbstractKokiModule(rest) {

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
}
```

### Imports

- No wildcard imports
- Import only what's needed
- Standard order:
    1. Spring imports
    2. koki-dto imports (grouped by domain)
    3. koki-platform imports
    4. Apache Commons imports
    5. Kotlin stdlib

## Architecture

### Package Structure

```
com.wutsi.koki.sdk/
├── AbstractKokiModule.kt       # Base class for file upload support
├── URLBuilder.kt               # URL construction and query encoding
├── KokiAuthentication.kt       # Authentication API client
├── KokiAccounts.kt             # Account management API client
├── KokiAgent.kt                # Agent management API client
├── KokiConfiguration.kt        # Configuration API client
├── KokiContacts.kt             # Contact management API client
├── KokiFiles.kt                # File upload/download API client
├── KokiInvitations.kt          # Invitation management API client
├── KokiLead.kt                 # Lead management API client
├── KokiListings.kt             # Listing management API client
├── KokiMessages.kt             # Message API client
├── KokiModules.kt              # Module management API client
├── KokiNotes.kt                # Note management API client
├── KokiOffer.kt                # Offer management API client
├── KokiOfferVersion.kt         # Offer version API client
├── KokiRefData.kt              # Reference data API client
├── KokiRoles.kt                # Role management API client
├── KokiTenants.kt              # Tenant management API client
├── KokiTypes.kt                # Type management API client
└── KokiUsers.kt                # User management API client
```

### Component Responsibilities

#### AbstractKokiModule

- Provides multipart file upload functionality
- Uses Spring's RestTemplate and MultipartFile
- Handles content disposition and form-data encoding
- Child classes access via protected `upload()` method

#### URLBuilder

- Constructs complete URLs from base URL and path
- Encodes query parameters with UTF-8
- Serializes collections as repeated parameters
- Filters out null values
- Handles URL encoding for special characters

#### SDK Client Classes

Each client wraps a specific domain's REST API:

- Accept DTOs from `koki-dto` package
- Build URLs using URLBuilder
- Execute HTTP operations via RestTemplate
- Return typed responses
- Handle domain-specific operations (CRUD + custom operations)

### Dependency Injection

SDK clients are designed for Spring dependency injection:

```kotlin
@Configuration
class KokiSdkConfiguration {
    @Bean
    fun urlBuilder(@Value("\${koki.server.url}") baseUrl: String) = URLBuilder(baseUrl)

    @Bean
    fun kokiAccounts(urlBuilder: URLBuilder, rest: RestTemplate) =
        KokiAccounts(urlBuilder, rest)

    @Bean
    fun kokiListings(urlBuilder: URLBuilder, rest: RestTemplate) =
        KokiListings(urlBuilder, rest)
}
```

### Infrastructure Integration

#### Tenant Context

- Some clients require `TenantProvider` (from koki-platform)
- Used to inject tenant ID in query parameters
- Example: `KokiFiles` uses tenant ID for upload URLs

#### Authentication

- Some clients require `AccessTokenHolder` (from koki-platform)
- Used to inject JWT tokens in query parameters or headers
- Example: `KokiFiles` uses access token for upload URLs

### URL Path Conventions

All REST endpoints follow standard conventions:

- Base path: `/v1/<domain>`
- Get by ID: `/v1/<domain>/{id}`
- Create: `/v1/<domain>` (POST)
- Update: `/v1/<domain>/{id}` (POST)
- Delete: `/v1/<domain>/{id}` (DELETE)
- Search: `/v1/<domain>` (GET with query params)
- Sub-resource update: `/v1/<domain>/{id}/<aspect>` (POST)
- Upload: `/v1/<domain>/csv` or `/v1/<domain>/upload` (POST multipart)

## Testing Guidelines

### Current State

- **No unit tests**: SDK clients are thin wrappers over RestTemplate
- Testing happens at integration level in consuming applications
- Mock SDK clients in application tests using libraries like Mockito

### When Tests Would Be Required

- If complex business logic is added to SDK clients
- If custom URL encoding or parameter handling logic is added
- If URLBuilder logic becomes more complex
- For validation of file upload mechanics

### Test Framework (if needed)

- JUnit 5
- Mockito for mocking RestTemplate
- MockRestServiceServer for integration testing
- Kotlin test utilities

### Testing Strategy in Consumer Applications

```kotlin
@MockBean
lateinit var kokiAccounts: KokiAccounts

@Test
fun `should retrieve account`() {
    val expected = GetAccountResponse(account = Account(id = 1, name = "Test"))
    `when`(kokiAccounts.account(1)).thenReturn(expected)

    val result = service.getAccount(1)

    assertThat(result.account.name).isEqualTo("Test")
}
```

## Documentation Guidelines

### Class Documentation

- Add KDoc comments for SDK client classes with:
    - Brief description of the domain
    - Reference to corresponding REST API documentation
    - Example usage if non-standard

**Example:**

```kotlin
/**
 * SDK client for Account Management API.
 *
 * Provides methods for CRUD operations on accounts and account attributes.
 *
 * @see com.wutsi.koki.account.dto
 */
class KokiAccounts(...)
```

### Method Documentation

- Not required for standard CRUD operations (self-explanatory)
- Document methods with non-obvious behavior
- Document specialized update methods with aspect description
- Document search parameters if complex

### README.md

- Must document:
    - Purpose and scope of SDK
    - Complete list of SDK clients with key methods
    - Tech stack with badges
    - Architecture diagram showing data flow
    - Setup and installation instructions
    - Usage examples for common operations
    - Integration with Spring Boot
    - License information

## Behavior

### Error Handling

- SDK does not catch exceptions - let them propagate to caller
- RestTemplate throws specific exceptions:
    - `HttpClientErrorException`: 4xx errors (client errors)
    - `HttpServerErrorException`: 5xx errors (server errors)
    - `ResourceAccessException`: Network/connectivity errors
- Consuming applications should handle exceptions appropriately

### Response Handling

- Always return response body directly
- Use `.body` to extract response from ResponseEntity
- Use `!!` for GET operations where response is guaranteed
- Omit for operations that don't return data (updates, deletes)

### Null Safety

- Query parameters can be nullable - URLBuilder filters them
- Response bodies are non-null for GET operations
- Use `?` for optional constructor parameters

### Immutability

- SDK clients are stateless (no mutable state)
- Thread-safe by design
- Can be singleton beans in Spring context

### Backward Compatibility

- **Never change method signatures** (add new methods instead)
- **Never change parameter types** (add overloaded methods instead)
- **Always add optional parameters at end** with default values
- Version in POM follows semantic versioning

### Evolution Patterns

#### Adding New Operations

```kotlin
// Add new method to existing SDK client
fun updateSpecialAspect(id: Long, request: UpdateSpecialAspectRequest) {
    val url = urlBuilder.build("$PATH_PREFIX/$id/special-aspect")
    rest.postForEntity(url, request, Any::class.java)
}
```

#### Adding New SDK Client

1. Create new file: `Koki<Domain>.kt`
2. Follow class structure pattern
3. Extend `AbstractKokiModule` if file upload needed
4. Define `PATH_PREFIX` in companion object
5. Implement CRUD operations following standard patterns
6. Add to documentation and README

#### Deprecating Operations

```kotlin
@Deprecated("Use updateNewVersion() instead", ReplaceWith("updateNewVersion(id, request)"))
fun updateOld(id: Long, request: OldRequest) {
    ...
}
```

### Dependencies

#### Required Dependencies

- **koki-dto**: Always required (compile scope)
- **koki-platform**: Required for tenant/auth context (compile scope)
- **Apache Commons IO**: Required for file upload (compile scope)

#### Provided Dependencies

- **Spring Web**: RestTemplate (provided scope - must be in consuming app)
- **Spring Boot**: Configuration support (provided scope - must be in consuming app)

#### No Dependencies On

- **No database libraries**: Pure HTTP client
- **No serialization libraries**: RestTemplate handles JSON with Jackson
- **No validation libraries**: Validation happens server-side
- **No logging frameworks**: Use SLF4J from Spring if needed

### Build Behavior

- Built with Maven: `mvn clean install`
- Deployed to GitHub Packages
- No code formatting enforcement (handled by ktlint in parent)
- No code coverage requirements (no tests)
- Packaged as JAR for library consumption

### RestTemplate Configuration

SDK clients expect RestTemplate to be configured with:

- **Jackson for JSON**: Automatic serialization/deserialization
- **Interceptors for headers**: Tenant-ID, Authorization (JWT token)
- **Error handler**: Standard error response parsing
- **Message converters**: JSON, multipart/form-data

**Example Configuration:**

```kotlin
@Bean
fun restTemplate(
    tenantProvider: TenantProvider,
    accessTokenHolder: AccessTokenHolder
): RestTemplate {
    val restTemplate = RestTemplate()
    restTemplate.interceptors.add { request, body, execution ->
        request.headers.add("X-Tenant-ID", tenantProvider.id().toString())
        accessTokenHolder.get()?.let {
            request.headers.add("Authorization", "Bearer $it")
        }
        execution.execute(request, body)
    }
    return restTemplate
}
```

