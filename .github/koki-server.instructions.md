---
module: koki-server
date: 2025-12-06
description: Backend REST API server for the Koki multi-tenant real estate platform providing comprehensive property management, listing discovery, lead tracking, and analytics capabilities.
applyTo: modules/koki-server/**
---

# Koki Server - Module Instructions

## Tech Stack

### Programming Languages

- **Kotlin** - Primary language for application code
- **Java 17** - Runtime environment

### Frameworks & Libraries

- **Spring Boot 3.x** - Application framework
- **Spring Security** - Authentication and authorization (JWT-based)
- **Spring Data JPA** - Data persistence with Hibernate ORM
- **Spring Cache** - Caching abstraction with Redis support
- **Spring Mail** - Email integration
- **SpringDoc OpenAPI** - API documentation with Swagger UI

### Database & Persistence

- **MySQL 8.x** - Primary relational database
- **Flyway** - Database migration and versioning
- **HikariCP** - Connection pooling

### Messaging & Events

- **RabbitMQ** - Message broker for domain event publishing and asynchronous processing

### File Processing

- **Apache Tika** - Document content extraction and text analysis
- **Apache POI** - Microsoft Office document processing
- **PDFBox** - PDF document processing
- **Flying Saucer** - PDF generation from HTML
- **jsoup** - HTML parsing and sanitization

### Templating & Rendering

- **Thymeleaf** - Template engine for email and PDF generation
- **Mustache** - Lightweight templating engine

### Utilities

- **Auth0 JWT** - JWT token creation and validation
- **libphonenumber** - International phone number validation
- **commons-validator** - Email and URL validation
- **commons-csv** - CSV file processing

### Build & Testing

- **Maven** - Build tool and dependency management
- **JUnit 5** - Testing framework
- **Kotlin Test** - Kotlin-specific testing utilities
- **GreenMail** - Email testing framework
- **JaCoCo** - Code coverage analysis (92% threshold enforced)
- Build Instructions
    - Always skip linting checks when running maven with the flag `-Dktlint.skip=true`.
    - After completing all the code changes, run `ktlint -F` to fix code style issues.

### Cloud & Storage

- **AWS S3** - File storage backend (with local filesystem fallback)
- **Heroku** - Deployment platform

## Coding Style and Idioms

### General Kotlin Style

- Follow Kotlin coding conventions with ktlint enforcement
- Use data classes for immutable DTOs and entities where appropriate
- Prefer `val` over `var` for immutability
- Use nullable types (`?`) explicitly and handle nullability with safe calls (`?.`) and Elvis operator (`?:`)
- Use named parameters for constructors with multiple parameters
- Leverage Kotlin's null-safety features instead of Optional

### Naming Conventions

- **Classes**: PascalCase (e.g., `AccountService`, `ListingEntity`)
- **Functions**: camelCase (e.g., `createAccount`, `searchListings`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `USER_ID`, `TENANT_ID`)
- **Package Names**: lowercase, dot-separated (e.g., `com.wutsi.koki.account.server.service`)

### Annotations and Conventions

- Service classes: `@Service`
- REST controllers: `@RestController` with `@RequestMapping`
- Configuration: `@Configuration`
- Repositories: `@Repository` extending Spring Data interfaces
- Transactional operations: `@Transactional` on service methods
- Request validation: `@Valid` on controller request parameters
- Tenant context: `@RequestHeader(name = "X-Tenant-ID")` for all endpoints

### Code Organization

- Use companion objects for constants and static factories
- Group related functionality in domain-specific packages
- Keep public API minimal; prefer internal visibility for implementation details
- Use extension functions for utility operations on specific types

### Error Handling

- Use custom exceptions from `com.wutsi.koki.error.exception` package
- Throw `NotFoundException` with `ErrorCode.RESOURCE_NOT_FOUND` for missing entities
- Throw `ConflictException` for business rule violations
- Use `@RestControllerAdvice` for centralized exception handling
- Always include error codes in error responses

### Validation

- Use Jakarta Bean Validation annotations on DTOs (`@Valid`, `@NotNull`, `@NotEmpty`, etc.)
- Perform business rule validation in service layer, not controllers
- Validate tenant ownership before any read/write operations

### Null Safety

- Use nullable types explicitly when null is a valid value
- Leverage Kotlin's `?.let`, `?.takeIf`, and Elvis operator for clean null handling
- Use `orElseThrow` with custom exceptions for repository operations
- Avoid !! (not-null assertion operator) except in test code

## Architecture

### Layered Architecture Pattern

```
Presentation Layer (Endpoints)
         ↓
  Business Logic Layer (Services)
         ↓
   Data Access Layer (Repositories/DAOs)
         ↓
      Database (MySQL)
```

### Folder Structure

Each domain module follows this consistent structure:

```
{domain}/
├── server/
│   ├── endpoint/          # REST API controllers
│   │   └── *Endpoints.kt  # REST endpoints (e.g., AccountEndpoints)
│   ├── service/           # Business logic
│   │   └── *Service.kt    # Service classes (e.g., AccountService)
│   ├── dao/               # Data access objects
│   │   └── *Repository.kt # Spring Data repositories
│   ├── domain/            # JPA entities
│   │   └── *Entity.kt     # Database entities
│   ├── mapper/            # DTO-Entity mappers
│   │   └── *Mapper.kt     # Mapping logic
│   ├── config/            # Module-specific configuration
│   └── mq/                # Message queue consumers (optional)
```

### Domain Modules

- **account/** - Account and attribute management
- **agent/** - Agent and user management, metrics
- **ai/** - AI service integration (LLM providers)
- **config/** - Cross-cutting configuration (security, messaging, etc.)
- **contact/** - Contact management
- **email/** - Email sending and templating
- **error/** - Error handling and custom exceptions
- **file/** - File upload, storage, and processing
- **lead/** - Lead tracking and management
- **listing/** - Property listing management (core business domain)
- **message/** - Messaging and notifications
- **module/** - Module and permission management
- **note/** - Note taking and management
- **offer/** - Offer and offer version management
- **refdata/** - Reference data (locations, amenities, categories)
- **security/** - Authentication, authorization, JWT handling
- **tenant/** - Multi-tenancy support and configuration
- **translation/** - Translation service integration

### Key Components

#### 1. Endpoints (Presentation Layer)

- Annotated with `@RestController` and `@RequestMapping`
- Handle HTTP requests and responses
- Accept `X-Tenant-ID` header on all endpoints for multi-tenancy
- Validate input using `@Valid` annotation
- Return DTOs from `koki-dto` module
- Delegate business logic to service layer
- Keep thin - no business logic

Example:

```kotlin
@RestController
@RequestMapping("/v1/accounts")
class AccountEndpoints(
    private val service: AccountService,
    private val mapper: AccountMapper,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreateAccountRequest,
    ): CreateAccountResponse {
        val account = service.create(request, tenantId)
        return CreateAccountResponse(account.id ?: -1)
    }
}
```

#### 2. Services (Business Logic Layer)

- Annotated with `@Service`
- Contain business logic and orchestration
- Use `@Transactional` for operations that modify data
- Enforce tenant isolation - always validate `tenantId` matches entity
- Throw custom exceptions for error conditions
- Publish domain events to RabbitMQ via `Publisher`
- Use constructor injection for dependencies

Example:

```kotlin
@Service
class AccountService(
    private val dao: AccountRepository,
    private val securityService: SecurityService,
    private val em: EntityManager,
) {
    @Transactional
    fun create(request: CreateAccountRequest, tenantId: Long): AccountEntity {
        val userId = securityService.getCurrentUserIdOrNull()
        return dao.save(
            AccountEntity(
                tenantId = tenantId,
                name = request.name,
                createdById = userId,
                modifiedById = userId,
            )
        )
    }
}
```

#### 3. Repositories (Data Access Layer)

- Annotated with `@Repository`
- Extend Spring Data interfaces (`CrudRepository`, `JpaRepository`)
- Define custom query methods using Spring Data naming conventions
- Use `@Query` for complex queries when needed
- Keep simple - no business logic

Example:

```kotlin
@Repository
interface AccountRepository : CrudRepository<AccountEntity, Long> {
    fun findByEmailAndTenantId(email: String, tenantId: Long): AccountEntity?
}
```

#### 4. Entities (Domain Model)

- Annotated with `@Entity` and `@Table`
- Use `@Id` with `@GeneratedValue(strategy = GenerationType.IDENTITY)` for primary keys
- Use Kotlin data classes with nullable properties where appropriate
- Name table with `T_` prefix (e.g., `T_ACCOUNT`)
- Name foreign key columns with `_fk` suffix (e.g., `tenant_fk`)
- Use `@Column` to map to database columns (snake_case)
- All entities must have `tenantId` for multi-tenancy

Example:

```kotlin
@Entity
@Table(name = "T_ACCOUNT")
data class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "tenant_fk") val tenantId: Long = -1,
    val name: String = "",
    val email: String = "",
    @Column(name = "created_by_fk") val createdById: Long? = null,
    val createdAt: Date = Date(),
)
```

#### 5. Mappers

- Annotated with `@Service`
- Convert between entities and DTOs
- Keep mapping logic centralized and reusable
- Handle nullable fields appropriately

Example:

```kotlin
@Service
class AccountMapper {
    fun toAccount(entity: AccountEntity): Account {
        return Account(
            id = entity.id ?: -1,
            name = entity.name,
            email = entity.email,
        )
    }
}
```

### Multi-Tenancy

All operations must respect tenant boundaries:

1. Every endpoint accepts `X-Tenant-ID` header
2. All entities have `tenantId` field
3. All service methods validate `tenantId` matches entity's tenant
4. Throw `NotFoundException` if tenant doesn't match
5. Filter by `tenantId` in all repository queries

### Event Publishing

Domain events are published to RabbitMQ for asynchronous processing:

- Use `Publisher` bean to publish events
- Events are defined in `koki-dto` module
- Configure MQ consumers in `{domain}/server/mq/` package
- Use `@Configuration` to set up queue bindings

### Database Migrations

- Use Flyway for all schema changes
- Migrations in `src/main/resources/db/migration/common/`
- Name pattern: `V{version}__{description}.sql` (e.g., `V1_4__account.sql`)
- Never modify existing migrations
- Test migrations on clean database

## Testing Guidelines

### Test Structure

Tests follow the same package structure as main code:

```
src/test/kotlin/com/wutsi/koki/
├── {domain}/
│   └── server/
│       ├── endpoint/      # Integration tests for endpoints
│       └── service/       # Unit tests for services
```

### Test Types

#### 1. Endpoint Integration Tests

- Extend `AuthorizationAwareEndpointTest` or `TenantAwareEndpointTest`
- Use `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`
- Use `@Sql` to load test data from SQL files
- Test data in `src/test/resources/db/test/{domain}/*.sql`
- Use `rest` (TestRestTemplate) to make HTTP requests
- Assert HTTP status codes and response bodies
- Verify database state using repositories

Example:

```kotlin
@Sql(value = ["/db/test/clean.sql", "/db/test/account/CreateAccountEndpoint.sql"])
class CreateAccountEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: AccountRepository

    @Test
    fun create() {
        val request = CreateAccountRequest(name = "Test Account")
        val response = rest.postForEntity("/v1/accounts", request, CreateAccountResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        val account = dao.findById(response.body!!.accountId).get()
        assertEquals("Test Account", account.name)
    }
}
```

#### 2. Service Unit Tests

- Use `@SpringBootTest` for tests requiring full context
- Use JUnit 5 (`@Test`) or Kotlin Test (`kotlin.test.Test`)
- Mock external dependencies when needed
- Focus on business logic validation

Example:

```kotlin
class EmailTemplateResolverTest {
    private val resolver = EmailTemplateResolver(
        MustacheTemplatingEngine(DefaultMustacheFactory())
    )

    @Test
    fun resolve() {
        val text = resolver.resolve("/email/template.html", mapOf("name" to "Ray"))
        assertEquals("Hello world!\nMy name is Ray", text.trim())
    }
}
```

### Test Data Management

- Clean database before each test: `/db/test/clean.sql`
- Load test data with descriptive file names: `/db/test/{domain}/{TestName}.sql`
- Use consistent test IDs across test files
- Test tenant ID: `TENANT_ID = 1L`
- Test user ID: `USER_ID = 11L`

### Test Coverage

- Maintain 92% line and class coverage (enforced by JaCoCo)
- Test happy paths and error scenarios
- Test tenant isolation
- Test validation rules
- Test authorization requirements

### Assertions

- Use Kotlin test assertions: `assertEquals`, `assertTrue`, `assertNull`, etc.
- Use `assertThrows` for exception testing
- Be specific in assertions - test exact values, not just non-null

## Documentation Guidelines

### Code Documentation

- Document public APIs with KDoc comments
- Explain business logic and complex algorithms
- Document assumptions and constraints
- Include examples for non-obvious usage

### API Documentation

- Use SpringDoc OpenAPI annotations for API documentation
- Annotate endpoints with `@Operation`, `@Parameter`, `@ApiResponse`
- Document request/response schemas in DTO module
- Keep Swagger UI organized by domain groups

### README and Setup

- Keep README.md up to date with features and architecture
- Document setup steps in SETUP.md
- Include configuration requirements
- Provide local development instructions

## Behavior

### Multi-Tenancy

- **ALWAYS** validate tenant ownership before any operation
- **NEVER** expose data from one tenant to another
- Include `tenantId` in all database queries
- Return 404 (NotFoundException) if tenant doesn't match, not 403

### Security

- Authenticate all endpoints (except login)
- Extract current user from JWT token via `SecurityService`
- Store `createdById` and `modifiedById` on all entities
- Validate permissions based on user roles and module permissions

### Error Handling

- Return appropriate HTTP status codes (400, 404, 409, 500)
- Use custom exceptions with error codes
- Include descriptive error messages
- Log errors with appropriate severity

### Validation

- Validate input at controller layer with Bean Validation
- Validate business rules at service layer
- Fail fast - validate early in the request lifecycle
- Provide clear validation error messages

### Performance

- Use `@Cacheable` for reference data and frequently accessed entities
- Lazy load relationships in JPA entities
- Use pagination for list endpoints (limit/offset)
- Optimize N+1 queries with JOIN FETCH or batch loading

### Transactions

- Use `@Transactional` on service methods that modify data
- Keep transactions short and focused
- Avoid long-running operations in transactions
- Publish events after transaction commits

### Logging

- Use SLF4J logger: `LoggerFactory.getLogger(ClassName::class.java)`
- Log important business events at INFO level
- Log errors with full stack traces at ERROR level
- Avoid logging sensitive data (passwords, tokens)

### Backward Compatibility

- Maintain API backward compatibility
- Deprecate before removing endpoints
- Use versioned API paths (`/v1/`, `/v2/`)
- Document breaking changes in release notes

