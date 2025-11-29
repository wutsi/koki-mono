<!--
module: koki-platform
date: November 20, 2025
description: Shared infrastructure library providing reusable, production-ready abstractions for cross-cutting concerns across the Koki platform, including storage, caching, messaging, multi-tenancy, and AI integration.
applyTo: modules/koki-platform/**
-->

# Copilot Instructions for koki-platform

## Tech Stack

### Language
- **Kotlin 2.1.0**: Primary language for all platform classes
- **Java 17**: Target JVM version

### Build Tools
- **Maven 3.8+**: Dependency management and build automation
- **Spring Boot Parent 3.5.7**: Parent POM for dependency version management

### Frameworks & Libraries
- **Spring Boot 3.5.7**: Core framework for configuration and dependency injection
- **Spring Security 6.x**: Authentication and authorization framework
- **Hibernate 6.x**: ORM framework (provided scope)
- **koki-dto**: Shared DTOs for domain models

### Infrastructure
- **AWS S3**: Cloud storage service
- **AWS Translate**: Translation service
- **Redis (Lettuce 7.0)**: Distributed caching
- **RabbitMQ (amqp-client 5.27.1)**: Message queue
- **Google Gemini**: AI/LLM provider

### Libraries
- **Mustache (0.9.14)**: Template engine
- **Apache Commons IO**: File utilities
- **Apache Commons Lang3**: String and utility functions
- **Apache PDFBox**: PDF processing
- **Hibernate Types 60**: Custom Hibernate types for JSON/arrays
- **SLF4J**: Logging facade (provided scope)

### Testing
- **JUnit 5**: Unit testing framework
- **Kotlin Test**: Kotlin testing utilities
- **Mockito**: Mocking framework
- **Code Coverage**: Jacoco with 85% line and class coverage thresholds

### Packaging
- **JAR**: Packaged as a library for consumption by other modules
- **GitHub Packages**: Distributed via GitHub Maven repository

## Coding Style and Idioms

### File Organization
- One class/interface per file
- File name must match the class/interface name
- Package structure: `com.wutsi.koki.platform.<module>`
- Modules: ai, cache, debug, executor, geoip, logger, messaging, mq, security, storage, templating, tenant, tracing, tracking, translation, url, util

### Interface Design Pattern
- **Provider Interface**: Define contract for infrastructure services
- **Multiple Implementations**: Provide different implementations (local, cloud, test)
- **Builder Pattern**: Use builders for complex object creation
- **Configuration Classes**: Spring Boot auto-configuration for wiring

**Example:**
```kotlin
// Interface
interface StorageService {
    fun store(path: String, content: InputStream, contentType: String?, contentLength: Long): URL
    fun get(url: URL, os: OutputStream)
    fun toURL(path: String): URL
}

// Implementation
class LocalStorageService(
    private val directory: String,
    private val baseUrl: String
) : StorageService {
    override fun store(...): URL { ... }
}

// Builder
class LocalStorageServiceBuilder {
    fun build(directory: String, baseUrl: String): StorageService {
        return LocalStorageService(directory, baseUrl)
    }
}

// Configuration
@Configuration
@ConditionalOnProperty(name = ["koki.storage.type"], havingValue = "local")
class LocalStorageConfiguration {
    @Bean
    fun storageService(...): StorageService { ... }
}
```

### Class Patterns

#### Interface Classes
- Define contracts for infrastructure services
- Use simple, clear method signatures
- Throw checked exceptions where appropriate (IOException)
- Document with KDoc

#### Implementation Classes
- Implement interface contracts
- Constructor dependency injection
- All properties as constructor parameters
- Use `private val` for dependencies
- No mutable state unless necessary

#### Configuration Classes
- Use `@Configuration` annotation
- Conditional loading with `@ConditionalOnProperty`
- Define beans with `@Bean` annotation
- Use `@Value` for configuration properties
- Group related beans together

#### Builder Classes
- Pattern: `<Service>Builder`
- Provide fluent API for object creation
- Handle complex initialization logic
- Validate required parameters

#### Health Indicator Classes
- Implement Spring Boot `HealthIndicator` interface
- Check infrastructure service health
- Return UP/DOWN status with details
- Pattern: `<Service>HealthIndicator`

### Naming Conventions

#### Interfaces
- Pattern: `<Service>` (e.g., `StorageService`, `CacheService`, `MessagingService`)
- No "I" prefix
- Use nouns describing the service

#### Implementations
- Pattern: `<Type><Service>` (e.g., `LocalStorageService`, `S3StorageService`, `RedisCache`)
- Indicate implementation type in name

#### Builders
- Pattern: `<Type>Builder` or `<Service>Builder`
- Examples: `LocalStorageServiceBuilder`, `SMTPMessagingServiceBuilder`

#### Configuration Classes
- Pattern: `<Type>Configuration` or `<Module>Configuration`
- Examples: `LocalCacheConfiguration`, `RedisCacheConfiguration`, `StorageConfiguration`

#### Exceptions
- Pattern: `<Context>Exception`
- Examples: `MessagingException`, `TranslationException`, `LLMException`
- Extend appropriate base exception

#### Providers
- Pattern: `<Context>Provider`
- Examples: `TenantProvider`, `ChannelTypeProvider`

#### Filters
- Pattern: `<Context>Filter`
- Examples: `TenantFilter`, `ChannelTypeFilter`

#### Interceptors
- Pattern: `<Context>Interceptor`
- Examples: `DebugRestInterceptor`, `AuthorizationRestInterceptor`

### Method Patterns

#### Service Interface Methods
```kotlin
// Store/persist operation
fun store(path: String, content: InputStream, ...): URL

// Retrieve operation
fun get(key: Any): ValueWrapper?

// Delete operation
fun delete(key: Any)

// Query operation
fun find(criteria: ...): List<Entity>
```

#### Builder Methods
```kotlin
fun build(config: Map<String, String>): Service {
    // Validate
    require(config.containsKey("key")) { "Missing required config" }

    // Build
    return ServiceImpl(config["key"]!!, ...)
}
```

#### Configuration Bean Methods
```kotlin
@Bean
fun serviceName(
    @Value("\${property.key}") value: String,
    dependency: Dependency
): ServiceInterface {
    return ServiceImpl(value, dependency)
}
```

### Exception Handling

#### Custom Exceptions
- Extend `RuntimeException` for unchecked exceptions
- Extend `Exception` for checked exceptions
- Provide meaningful error messages
- Include cause when wrapping exceptions

**Example:**
```kotlin
class MessagingException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

class TranslationNotConfiguredException :
    RuntimeException("Translation service not configured")
```

#### Throwing Exceptions
```kotlin
// With message
throw MessagingException("Failed to send email")

// With cause
throw MessagingException("SMTP connection failed", e)

// With require/check
require(value != null) { "Value cannot be null" }
check(isInitialized) { "Service not initialized" }
```

### Logging Patterns

#### KVLogger (Key-Value Logger)
```kotlin
class ServiceImpl(private val logger: KVLogger) {
    fun process(id: Long, data: String) {
        logger.add("operation", "process")
        logger.add("id", id)
        logger.add("data_length", data.length)
        try {
            // Process
            logger.add("status", "success")
        } catch (e: Exception) {
            logger.add("status", "error")
            logger.setException(e)
        } finally {
            logger.log()
        }
    }
}
```

### Tenant Awareness

#### Tenant-Aware Services
Services that need tenant context:
- Use `TenantProvider` to get current tenant ID
- Include tenant ID in cache keys
- Include tenant ID in storage paths
- Include tenant ID in log messages

**Example:**
```kotlin
class TenantAwareStorageService(
    private val storage: StorageService,
    private val tenantProvider: TenantProvider
) {
    fun store(filename: String, content: InputStream): URL {
        val tenantId = tenantProvider.id()
        val path = "tenants/$tenantId/files/$filename"
        return storage.store(path, content, null, content.available().toLong())
    }
}
```

### Imports
- No wildcard imports
- Import only what's needed
- Standard order:
  1. Java/JDK imports
  2. Spring imports
  3. Third-party library imports
  4. koki-dto imports
  5. koki-platform internal imports
  6. Kotlin stdlib

## Architecture

### Package Structure
```
com.wutsi.koki.platform/
├── KokiApplication.kt              # Main platform annotation
├── ai/                             # AI/LLM provider abstraction
│   ├── agent/                      # AI agents (ReAct pattern)
│   ├── llm/                        # LLM interface and implementations
│   │   ├── gemini/                 # Google Gemini provider
│   │   ├── deepseek/               # Deepseek provider
│   │   └── koki/                   # Koki LLM wrapper
│   └── config/                     # AI configuration
├── cache/                          # Caching abstraction
│   ├── local/                      # Local in-memory cache
│   ├── redis/                      # Redis distributed cache
│   └── config/                     # Cache configuration
├── debug/                          # Debug utilities
│   └── DebugRestInterceptor.kt     # REST request/response logging
├── executor/                       # Async execution
│   └── config/                     # Thread pool configuration
├── geoip/                          # GeoIP services
│   ├── impl/                       # GeoIP implementations
│   └── config/                     # GeoIP configuration
├── logger/                         # Structured logging
│   ├── KVLogger.kt                 # Key-value logger interface
│   ├── DefaultKVLogger.kt          # Default implementation
│   ├── servlet/                    # Servlet filter integration
│   └── config/                     # Logger configuration
├── messaging/                      # Email messaging
│   ├── MessagingService.kt         # Messaging interface
│   ├── smtp/                       # SMTP implementation
│   └── config/                     # Messaging configuration
├── mq/                             # Message queue (RabbitMQ)
│   ├── Publisher.kt                # MQ publisher interface
│   ├── Consumer.kt                 # MQ consumer interface
│   ├── rabbitmq/                   # RabbitMQ implementation
│   └── config/                     # MQ configuration
├── security/                       # Security integration
│   ├── AccessTokenHolder.kt        # Token holder interface
│   ├── JWTAuthentication.kt        # JWT authentication
│   ├── servlet/                    # Servlet filters
│   └── config/                     # Security configuration
├── storage/                        # File storage abstraction
│   ├── StorageService.kt           # Storage interface
│   ├── local/                      # Local filesystem storage
│   ├── s3/                         # AWS S3 storage
│   └── config/                     # Storage configuration
├── templating/                     # Template engine
│   ├── TemplatingEngine.kt         # Template interface
│   └── MustacheTemplatingEngine.kt # Mustache implementation
├── tenant/                         # Multi-tenancy
│   ├── TenantProvider.kt           # Tenant provider interface
│   ├── TenantHolder.kt             # Thread-local tenant holder
│   ├── filter/                     # Tenant resolution filters
│   └── config/                     # Tenant configuration
├── tracing/                        # Distributed tracing
│   ├── TracingContext.kt           # Trace context
│   └── config/                     # Tracing configuration
├── tracking/                       # Event tracking
│   ├── TrackingService.kt          # Tracking interface
│   ├── servlet/                    # Servlet filters for tracking
│   └── config/                     # Tracking configuration
├── translation/                    # Translation services
│   ├── TranslationService.kt       # Translation interface
│   ├── aws/                        # AWS Translate implementation
│   └── config/                     # Translation configuration
├── url/                            # URL utilities
│   ├── UrlShortener.kt             # URL shortening interface
│   └── BitlyUrlShortener.kt        # Bitly implementation
└── util/                           # General utilities
    ├── StringUtils.kt              # String utilities
    ├── HtmlUtils.kt                # HTML utilities
    └── CustomPhysicalNamingStrategy.kt # Hibernate naming
```

### Core Patterns

#### Provider Pattern
- Define interface for infrastructure service
- Provide multiple implementations for different environments
- Use configuration to select implementation at runtime

#### Builder Pattern
- Complex object creation with validation
- Fluent API for configuration
- Separate construction from representation

#### Configuration Pattern
- Spring Boot auto-configuration
- Conditional bean creation based on properties
- Default implementations with override capability

#### Health Check Pattern
- Implement `HealthIndicator` for each infrastructure service
- Provide detailed health information
- Enable monitoring and alerting

### Main Annotation
`@KokiApplication` - Meta-annotation that imports all platform configurations:
- AI Configuration
- Cache Configuration (Local, Redis, None)
- Executor Configuration
- GeoIP Configuration
- Logger Configuration
- MQ Configuration
- Storage Configuration

### Multi-Tenancy Architecture

#### Tenant Resolution
1. Request comes in with tenant identifier (header, cookie, subdomain)
2. Filter extracts tenant ID and stores in `TenantHolder`
3. Services access tenant ID via `TenantProvider`
4. Tenant ID propagates through cache keys, storage paths, logs

#### Tenant Isolation
- Storage paths: `tenants/{tenantId}/...`
- Cache keys: `{tenant}:{key}`
- Database queries: Filtered by tenant_id column
- Logs: Include tenant_id in structured logs

### Security Architecture

#### JWT Authentication
1. Request contains JWT token (Authorization header, cookie)
2. Filter validates and decodes JWT
3. Creates Spring Security `Authentication` object
4. Token available via `AccessTokenHolder`

#### Token Propagation
- `RequestAccessTokenHolder`: Request-scoped token storage
- `CookieAccessTokenHolder`: Cookie-based token storage
- Used by SDK clients to propagate tokens to downstream services

## Testing Guidelines

### Test Coverage Requirements
- **Line Coverage**: 85% minimum
- **Class Coverage**: 85% minimum
- Enforced by Jacoco in build

### Test Structure
- One test class per implementation class
- Test class name: `<ClassName>Test`
- Place in same package structure under `src/test/kotlin`

### Test Patterns

#### Unit Test Pattern
```kotlin
class ServiceImplTest {
    private lateinit var service: ServiceImpl
    private lateinit var dependency: Dependency

    @BeforeEach
    fun setup() {
        dependency = mock()
        service = ServiceImpl(dependency)
    }

    @Test
    fun `should perform operation successfully`() {
        // GIVEN
        val input = "test"
        `when`(dependency.process(input)).thenReturn("result")

        // WHEN
        val result = service.operation(input)

        // THEN
        assertEquals("expected", result)
        verify(dependency).process(input)
    }
}
```

#### Integration Test Pattern
```kotlin
@SpringBootTest
class StorageIntegrationTest {
    @Autowired
    private lateinit var storage: StorageService

    @Test
    fun `should store and retrieve file`() {
        // GIVEN
        val content = "Hello World"
        val input = ByteArrayInputStream(content.toByteArray())

        // WHEN
        val url = storage.store("test.txt", input, "text/plain", content.length.toLong())

        // THEN
        val output = ByteArrayOutputStream()
        storage.get(url, output)
        assertEquals(content, output.toString())
    }
}
```

#### Health Indicator Test Pattern
```kotlin
class ServiceHealthIndicatorTest {
    @Test
    fun `should return UP when service is healthy`() {
        // GIVEN
        val service = mock<Service>()
        `when`(service.isHealthy()).thenReturn(true)
        val indicator = ServiceHealthIndicator(service)

        // WHEN
        val health = indicator.health()

        // THEN
        assertEquals(Status.UP, health.status)
    }
}
```

### Test Frameworks
- **JUnit 5**: Main testing framework
- **Kotlin Test**: Kotlin-specific assertions
- **Mockito**: Mocking dependencies
- **Spring Boot Test**: Integration testing support

### Mocking Guidelines
- Mock external dependencies (databases, APIs, file systems)
- Don't mock value objects or DTOs
- Use `mock()` for interface/class mocking
- Use `verify()` to check interactions

## Documentation Guidelines

### Interface Documentation
- Add KDoc for all public interfaces
- Document method parameters and return values
- Include usage examples
- Document exceptions

**Example:**
```kotlin
/**
 * Storage service for file operations.
 *
 * Provides abstraction for storing and retrieving files from various storage backends.
 *
 * Example:
 * ```
 * val url = storage.store("path/file.txt", inputStream, "text/plain", 1024)
 * storage.get(url, outputStream)
 * ```
 */
interface StorageService {
    /**
     * Stores a file at the specified path.
     *
     * @param path Relative path for the file
     * @param content Input stream containing file content
     * @param contentType MIME type (optional)
     * @param contentLength Size in bytes
     * @return URL to access the stored file
     * @throws IOException if storage operation fails
     */
    @Throws(IOException::class)
    fun store(path: String, content: InputStream, contentType: String?, contentLength: Long): URL
}
```

### Implementation Documentation
- Document complex algorithms
- Explain non-obvious implementation decisions
- Document thread safety considerations
- Reference relevant RFCs or specs

### Configuration Documentation
- Document all configuration properties
- Include default values
- Explain when to use each configuration
- Provide examples

### README.md
- Must document:
  - Purpose and scope of platform
  - Complete list of modules with descriptions
  - Tech stack with badges
  - Architecture overview
  - Usage examples for each module
  - Configuration guide
  - Testing approach
  - License information

## Behavior

### Error Handling
- Use exceptions for error conditions
- Provide meaningful error messages
- Include context in exceptions
- Log errors with structured logging
- Don't swallow exceptions

### Async Operations
- Use Spring's `@Async` for background tasks
- Configure thread pools via `ExecutorConfiguration`
- Propagate MDC context to async threads
- Handle exceptions in async methods

### Caching Strategy
- Use Spring Cache abstraction (`@Cacheable`, `@CacheEvict`)
- Include tenant ID in cache keys
- Set appropriate TTL values
- Handle cache misses gracefully
- Provide cache bypass for debugging

### Storage Patterns
- Organize files by tenant: `tenants/{tenantId}/...`
- Use consistent path structure
- Support both local and cloud storage
- Handle file not found gracefully
- Clean up temporary files

### Messaging Patterns
- Template-based message composition
- Support HTML and plain text
- Handle multiple recipients
- Retry on transient failures
- Log all message sends

### Translation Patterns
- Cache translations to reduce API calls
- Auto-detect source language
- Batch translations when possible
- Handle unsupported languages gracefully
- Fallback to original text on failure

### Security Patterns
- Validate JWT tokens
- Extract claims and create authentication
- Propagate security context
- Log security events
- Handle token expiration

### Backward Compatibility
- **Never break existing interfaces** (add new methods instead)
- **Never change method signatures** (create new interface version)
- **Deprecate before removal** (mark with `@Deprecated`)
- Version in POM follows semantic versioning

### Evolution Patterns

#### Adding New Module
1. Create package: `com.wutsi.koki.platform.<module>`
2. Define interface
3. Create implementation(s)
4. Add builder if needed
5. Create configuration class
6. Add health indicator
7. Write tests (85% coverage)
8. Document in README

#### Adding New Implementation
1. Create class implementing interface
2. Add builder for complex initialization
3. Create configuration with `@ConditionalOnProperty`
4. Add health indicator
5. Write tests
6. Update documentation

### Dependencies

#### Required Dependencies (Compile Scope)
- **koki-dto**: Domain models
- **AWS SDK S3**: Cloud storage
- **AWS SDK Translate**: Translation
- **RabbitMQ Client**: Message queue
- **Lettuce**: Redis client
- **Mustache**: Template engine
- **Apache Commons IO, Lang3**: Utilities
- **Apache PDFBox**: PDF processing
- **Hibernate Types 60**: Custom Hibernate types

#### Provided Dependencies
- **Hibernate Core**: ORM (provided by consuming application)
- **SLF4J**: Logging (provided by consuming application)

#### Optional Dependencies
- Spring Boot Starter Web (for filters and interceptors)
- Spring Security (for authentication)
- Spring Cache (for caching)

### Build Behavior
- Built with Maven: `mvn clean install`
- Runs unit tests with Jacoco coverage
- Enforces 85% line and class coverage
- Deployed to GitHub Packages
- Code formatting enforced by ktlint (from parent)
- Packaged as JAR for library consumption

### Configuration Best Practices

#### Property Naming
- Use kebab-case: `koki.storage.type`
- Prefix with module: `koki.<module>.<property>`
- Use hierarchical structure

#### Conditional Configuration
```kotlin
@Configuration
@ConditionalOnProperty(
    name = ["koki.storage.type"],
    havingValue = "s3"
)
class S3StorageConfiguration {
    @Bean
    fun storageService(...): StorageService { ... }
}
```

#### Default Values
```kotlin
@Value("\${koki.cache.ttl:3600}")
private val ttl: Int = 3600
```

### Performance Considerations
- Use caching to reduce database/API calls
- Implement connection pooling
- Use async processing for non-critical operations
- Monitor and log performance metrics
- Optimize hot paths

