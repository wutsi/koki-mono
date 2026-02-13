# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

### Full Build
```bash
# Build entire monorepo
mvn install -Dheadless=true

# Build specific module
cd modules/<module-name>
mvn package
```

### Testing
```bash
# Run all tests for a module
cd modules/<module-name>
mvn test

# Run single test class
mvn test -Dtest=ClassName

# Run single test method
mvn test -Dtest=ClassName#methodName

# Run with database configuration (for koki-server)
mvn test -Dspring.datasource.username=root -Dspring.datasource.password=root
```

### Code Quality
```bash
# ktlint is automatically run during validate phase
mvn validate

# Format code with ktlint
mvn antrun:run@ktlint-format

# Check test coverage (JaCoCo)
mvn verify
# Coverage thresholds: 98% line, 95% class (parent pom.xml)
# Module-specific thresholds may vary (e.g., koki-server: 92%)
```

### Running Services
```bash
# Start koki-server (port 8080)
cd modules/koki-server
mvn spring-boot:run

# Start koki-portal (port 8081)
cd modules/koki-portal
mvn spring-boot:run

# Start koki-portal-public
cd modules/koki-portal-public
mvn spring-boot:run

# Start koki-tracking-server
cd modules/koki-tracking-server
mvn spring-boot:run
```

## Architecture Overview

### Module Dependency Hierarchy
```
koki-dto (DTOs/contracts)
    ↓
koki-platform (infrastructure abstractions)
    ↓
koki-sdk (client library) & koki-server (REST API backend)
    ↓
koki-portal (admin UI) & koki-portal-public (public website) & koki-tracking-server (analytics)
```

**Key principle:** Lower layers should never depend on higher layers.

### Multi-Tenant Architecture

The platform uses **strict tenant isolation** with `X-Tenant-ID` header propagation:

- **Data Isolation:** All entities have `tenantId` field; queries automatically filtered by tenant context
- **Context Propagation:** `TenantHolder` (ThreadLocal) stores current tenant ID throughout request lifecycle
- **Security:** JWT tokens contain tenant ID; validated on every authenticated request
- **Storage/Cache:** Tenant ID used as key prefix in S3, Redis to ensure isolation

When working with data access:
- Most repositories extend Spring Data JPA and automatically filter by `tenantId`
- Always use tenant-scoped queries; never expose cross-tenant data
- Test classes inherit from `TenantAwareEndpointTest` (tenant ID = 100) or `AuthorizationAwareEndpointTest` (tenant ID = 100, user ID = 11)

### Service Layer Patterns

**koki-server** uses consistent layering per domain (listing, account, contact, lead, etc.):

```
endpoint/ - REST controllers (@RestController)
    ├─ service/ - Business logic
    ├─ dao/ - Spring Data JPA repositories
    ├─ domain/ - JPA entities (suffixed with Entity)
    ├─ mapper/ - DTO ↔ Entity conversion
    ├─ io/ - CSV import/export
    └─ config/ - Spring configuration
```

**Naming conventions:**
- Entities: `*Entity.kt` (e.g., `ListingEntity`, `UserEntity`)
- DTOs: Located in `koki-dto` module (e.g., `ListingDTO`, `CreateListingRequest`)
- Endpoints: `*Endpoints.kt` containing multiple related REST endpoints
- Services: `*Service.kt` (business logic)
- Repositories: `*Repository.kt` (data access)

### Database Migrations

**Flyway** manages schema versioning:
- Migrations: `modules/koki-server/src/main/resources/db/migration/`
- Convention: `V{version}__{description}.sql` (e.g., `V1_0__create_listing.sql`)
- Never modify existing migrations; always create new ones
- Migrations run automatically on application startup

### Testing Patterns

**Test structure:**
```kotlin
@Sql(value = ["/db/test/clean.sql", "/db/test/listing/UpdateListingEndpoint.sql"])
class UpdateListingEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: ListingRepository

    @Test
    fun `test description in backticks`() {
        // Arrange
        val request = CreateRequest(...)

        // Act
        val response = rest.postForEntity("/v1/listings", request, ResponseType::class.java)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        val entity = dao.findById(id).get()
        assertEquals(expected, entity.field)
    }
}
```

**Test base classes:**
- `TenantAwareEndpointTest`: Provides tenant context (tenant ID = 100)
- `AuthorizationAwareEndpointTest`: Adds JWT authentication (tenant ID = 100, user ID = 11)
- Test SQL fixtures in `src/test/resources/db/test/<domain>/` directory

**Test data setup:**
- Each test has dedicated SQL file with test fixtures
- Always include `/db/test/clean.sql` first to reset state
- SQL files should be named after the test class (e.g., `UpdateListingEndpointTest.kt` → `UpdateListingEndpoint.sql`)

### Infrastructure Abstractions (koki-platform)

Pluggable backends for:
- **Storage:** `StorageService` (local filesystem or AWS S3)
- **Cache:** `CacheService` (Redis)
- **Messaging:** RabbitMQ integration
- **Email:** `EmailService` abstraction
- **Translation:** AWS Translate integration
- **AI/LLM:** LLM service providers (Gemini, DeepSeek, etc.)

Use these abstractions rather than direct third-party library calls to maintain flexibility.

## Environment Setup

Required environment variables (see CONTRIBUTING.md):
- `GEMINI_API_KEY`: Google Gemini API key
- `STRIPE_API_KEY`: Stripe API key (test mode)
- `DEEPSEEK_API_KEY`: DeepSeek API key
- `TELEGRAM_TOKEN`: Telegram bot token (optional)
- `TELEGRAM_CHATBOT`: Telegram bot name (optional)

Local services:
- MySQL 9.6+ (root user with no password)
- RabbitMQ 4.2+
- Redis 7.0+ (optional, for caching)

## Key Configuration Files

- `pom.xml` (root): Parent POM with shared dependencies, compiler plugins, JaCoCo, ktlint
- `modules/*/pom.xml`: Module-specific dependencies and build configuration
- `application.yml`: Default Spring Boot configuration (MySQL, Flyway, JPA, Jackson)
- `application-test.yml`: Test profile configuration
- `application-prod.yml`: Production profile configuration
- `Procfile`: Heroku deployment configuration (per deployable module)

## Common Development Scenarios

### Adding a new endpoint

1. Create request/response DTOs in `koki-dto` module
2. Add endpoint method in appropriate `*Endpoints.kt` class in `koki-server`
3. Implement business logic in corresponding `*Service.kt`
4. Add repository method if needed in `*Repository.kt`
5. Create test class extending `AuthorizationAwareEndpointTest`
6. Create SQL fixture in `src/test/resources/db/test/<domain>/`

### Adding a new domain/module

1. Create package structure: `endpoint/`, `service/`, `dao/`, `domain/`, `mapper/`
2. Define entities with `@Entity` and `tenantId` field
3. Create repository extending `JpaRepository`
4. Create Flyway migration for schema
5. Add endpoints with proper security annotations
6. Create comprehensive tests with SQL fixtures

### Modifying database schema

1. Create new Flyway migration in `db/migration/`
2. Update corresponding entity classes
3. Regenerate or update DTOs in `koki-dto`
4. Update mappers if entity-DTO mapping changed
5. Update test SQL fixtures

## CI/CD

GitHub Actions workflows in `.github/workflows/`:
- `*-pr.yml`: Run on pull requests (build, test, coverage check)
- `*-master.yml`: Run on master branch (build, test, deploy)
- Coverage badges generated via JaCoCo and stored in `.github/badges/`

Services in CI:
- MySQL 9.6
- RabbitMQ 4.2
- Redis (with health checks)
