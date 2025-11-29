<!--
module: koki-tracking-server
date: November 21, 2025
description: Event-driven Spring Boot microservice that ingests tracking events from RabbitMQ, enriches them through modular filter pipelines (bot detection, traffic source attribution, geo-location, device classification), persists events to cloud storage in CSV format, and generates aggregated KPI metrics for analytics.
applyTo: modules/koki-tracking-server/**
-->

# Copilot Instructions for koki-tracking-server

## Tech Stack

### Language

- **Kotlin 2.1.0**: Primary language for all application code
- **Java 17**: Target JVM version

### Build Tools

- **Maven 3.8+**: Dependency management and build automation
- **Spring Boot Parent 3.5.7**: Parent POM for dependency version management

### Frameworks & Libraries

- **Spring Boot 3.5.7**: Core framework for application structure
- **Spring Web**: REST API endpoints (if needed)
- **Spring Security**: Basic authentication and security configuration
- **Spring Boot Actuator**: Health checks, metrics, and monitoring
- **Spring Cache**: Redis-based caching for GeoIP lookups
- **Spring Scheduling**: Cron-based job scheduling (`@EnableScheduling`)
- **Spring Async**: Non-blocking event processing (`@EnableAsync`)

### Message Queue

- **RabbitMQ**: Event ingestion from Koki platform components
- **koki-platform MQ**: Consumer and Publisher abstractions for RabbitMQ integration

### Data Processing

- **Apache Commons CSV**: CSV parsing and writing for event persistence
- **UAParser Java**: User-agent parsing for bot detection and device classification

### Storage

- **koki-platform Storage**: Abstraction layer supporting local filesystem and AWS S3
- **Local Storage**: File-based storage for development/testing
- **AWS S3**: Cloud storage for production event and KPI persistence

### Infrastructure

- **koki-dto**: Shared DTOs for tracking events and domain models
- **koki-platform**: Infrastructure support (logging, storage, GeoIP, traffic source detection)

### Testing

- **JUnit 5**: Test framework
- **Mockito-Kotlin**: Mocking framework (`mockitokotlin2`)
- **Spring Boot Test**: Integration testing support
- **Kotlin Test**: Kotlin-specific assertions

### Code Quality

- **JaCoCo**: Code coverage (97% line coverage, 91% class coverage required)
- **ktlint**: Kotlin code formatting (inherited from parent POM)

### Documentation

- **SpringDoc OpenAPI**: Auto-generated API documentation with Swagger UI

### Packaging

- **Executable JAR**: Packaged as Spring Boot application
- **Heroku**: Deployed via Procfile with system.properties for Java version

## Coding Style and Idioms

### File Organization

- One class per file
- File name must match the class name
- Package structure mirrors architectural layers:
    - `domain`: Data entities (data classes)
    - `service`: Business logic (services, consumers, filters)
    - `dao`: Data access objects (repositories)
    - `config`: Spring configuration classes
    - `job`: Scheduled job implementations

### Class Structure

#### Domain Entities

- Use `data class` for all domain entities
- Immutable by default (no `var`, only `val`)
- Provide default values for all properties
- Use nullable types for optional fields

**Example:**

```kotlin
data class TrackEntity(
    val time: Long = 0,
    val correlationId: String? = null,
    val deviceId: String? = null,
    val accountId: String? = null,
    val tenantId: Long? = null,
    val productId: String? = null,
    val ua: String? = null,
    val bot: Boolean = false,
    val ip: String? = null,
    val event: TrackEvent = TrackEvent.UNKNOWN,
    val channelType: ChannelType = ChannelType.UNKNOWN,
    val deviceType: DeviceType = DeviceType.UNKNOWN,
    val country: String? = null,
    val rank: Int? = null,
)
```

#### Service Classes

- Annotate with `@Service`
- Use constructor injection for dependencies
- No `@Autowired` annotations
- Keep methods focused and single-purpose

**Example:**

```kotlin
@Service
class TrackingConsumer(
    private val pipeline: Pipeline,
    private val logger: KVLogger,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is TrackSubmittedEvent) {
            onTrackSubmitted(event)
            return true
        }
        return false
    }

    private fun onTrackSubmitted(event: TrackSubmittedEvent) {
        // Implementation
    }
}
```

#### Filter Implementations

- Implement `Filter` interface
- Annotate with `@Service`
- Accept `TrackEntity` and return modified `TrackEntity`
- Use `copy()` to create modified entities (immutability)
- Handle null values gracefully

**Example:**

```kotlin
@Service
class BotFilter(private val logger: KVLogger) : Filter {
    private val uaParser = Parser()

    override fun filter(track: TrackEntity): TrackEntity {
        if (track.ua == null) {
            return track
        }
        val client = uaParser.parse(track.ua)
        val bot = client.device.family.equals("spider", true)
        logger.add("track_bot", bot)
        return track.copy(bot = bot)
    }
}
```

#### Repository/DAO Classes

- Annotate with `@Service` (not `@Repository`)
- Handle CSV parsing and writing
- Use Apache Commons CSV with explicit headers
- Delegate storage operations to `StorageService`

**Example:**

```kotlin
@Service
class TrackRepository(private val storageServiceBuilder: StorageServiceBuilder) {
    companion object {
        private val HEADERS = arrayOf("time", "correlation_id", "tenant_id", ...)
    }

    fun save(date: LocalDate, items: List<TrackEntity>): URL {
        // Implementation
    }

    fun read(input: InputStream): List<TrackEntity> {
        // Implementation
    }
}
```

#### Configuration Classes

- Annotate with `@Configuration`
- Use `@Bean` methods for component registration
- Constructor inject dependencies
- Use `@Value` for configuration properties

**Example:**

```kotlin
@Configuration
class PipelineConfiguration(
    private val persisterFilter: PersisterFilter,
    private val botFilter: BotFilter,
    private val countryFilter: CountryFilter,
    private val deviceTypeFilter: DeviceTypeFilter,
    private val sourceFilter: SourceFilter,
) {
    @Bean
    fun pipeline(): Pipeline {
        return Pipeline(
            listOf(
                botFilter,
                countryFilter,
                deviceTypeFilter,
                sourceFilter,
                persisterFilter // Must be last
            )
        )
    }
}
```

#### Job Classes

- Annotate with `@Service`
- Use `@Scheduled` with cron expressions from configuration
- Use `@PreDestroy` for cleanup logic
- Keep job logic minimal - delegate to services

**Example:**

```kotlin
@Service
class PersisterJob(private val filter: PersisterFilter) {
    @PreDestroy
    fun destroy() {
        filter.flush()
    }

    @Scheduled(cron = "\${koki.persister.cron}")
    fun run() {
        filter.flush()
    }
}
```

### Naming Conventions

#### Classes

- **Domain entities**: Suffix with `Entity` (e.g., `TrackEntity`, `KpiListingEntity`)
- **Services**: Descriptive noun (e.g., `TrackingConsumer`, `KpiListingGenerator`)
- **Filters**: Suffix with `Filter` (e.g., `BotFilter`, `SourceFilter`, `CountryFilter`)
- **Repositories**: Suffix with `Repository` (e.g., `TrackRepository`, `KpiListingRepository`)
- **Jobs**: Suffix with `Job` (e.g., `PersisterJob`, `KpiListingGeneratorJob`)
- **Configurations**: Suffix with `Configuration` (e.g., `PipelineConfiguration`)

#### Methods

- **Public methods**: Use descriptive verbs (e.g., `consume()`, `filter()`, `generate()`, `flush()`)
- **Private methods**: Prefix with action verb (e.g., `onTrackSubmitted()`, `toTrackEntity()`, `shouldFlush()`)
- **Data transformation**: Prefix with `to` (e.g., `toKpiRoomEntity()`, `toTrackEntity()`)
- **Boolean queries**: Prefix with `should` or `is` (e.g., `shouldFlush()`, `isBot()`)

#### Variables

- Use descriptive names without Hungarian notation
- Collections: Plural nouns (e.g., `tracks`, `filters`, `items`)
- Single objects: Singular nouns (e.g., `track`, `filter`, `entity`)
- Constants: UPPER_SNAKE_CASE in companion objects

### Logging Patterns

#### Structured Logging with KVLogger

- Use `KVLogger` from koki-platform for structured key-value logging
- Add context early in processing pipeline
- Log all relevant tracking attributes

**Example:**

```kotlin
logger.add("track_event", event.track.event)
logger.add("track_product_id", event.track.productId)
logger.add("track_account_id", event.track.accountId)
logger.add("track_bot", bot)
logger.add("track_country", country)
```

#### Standard Logging with SLF4J

- Use SLF4J logger for operational messages
- Define logger in companion object
- Use appropriate log levels:
    - `INFO`: Operational milestones (e.g., "Storing 100 tracking events")
    - `WARN`: Recoverable errors (e.g., "Unable to resolve location from IP")
    - `ERROR`: Critical errors (not used often - let exceptions propagate)

**Example:**

```kotlin
companion object {
    private val LOGGER = LoggerFactory.getLogger(KpiListingGenerator::class.java)
}

LOGGER.info("Storing ${copy.size} tracking events(s): $url")
LOGGER.warn("Error while processing $url", ex)
```

### Error Handling

- **Let exceptions propagate** - don't catch unless you can handle meaningfully
- **Use try-catch only for recoverable errors** (e.g., GeoIP lookup failures)
- **Log warnings for non-critical failures** (e.g., IP resolution)
- **Use companion object SLF4J logger** for exception logging
- **Return original entity on filter failures** (graceful degradation)

**Example:**

```kotlin
override fun filter(track: TrackEntity): TrackEntity {
    if (track.ip.isNullOrEmpty()) {
        return track
    }

    try {
        val country = service.resolve(track.ip)?.countryCode
        logger.add("track_country", country)
        return track.copy(country = country)
    } catch (ex: Exception) {
        LOGGER.warn("Unable to resolve location information from ${track.ip}", ex)
        return track
    }
}
```

### Immutability and Data Transformation

- **Always use `copy()`** to modify data class instances
- **Never mutate existing entities** - create new instances
- **Chain transformations** using functional style
- **Use `map()`, `filter()`, `groupBy()`** for collection operations

**Example:**

```kotlin
val tracks = loadTracks(url, storage)
    .filter { track -> !track.bot }
    .filter { track -> !track.productId.isNullOrEmpty() }
    .filter { track -> EVENTS.contains(track.event) }

val kpis = tracks.groupBy { track -> track.productId }
    .map { entry -> toKpiRoomEntity(entry.value) }
    .sortedBy { kpi -> kpi.productId }
```

### Null Safety

- Use nullable types (`?`) for optional fields
- Use safe calls (`?.`) for null-safe access
- Use Elvis operator (`?:`) for default values
- Use `isNullOrEmpty()` for string/collection checks
- Never use `!!` unless absolutely necessary

### Collection Handling

- Use Kotlin collection APIs: `map()`, `filter()`, `groupBy()`, `sortedBy()`
- Use `mapNotNull()` to filter nulls during transformation
- Use `toSet()` for uniqueness (e.g., counting unique visitors)
- Use synchronized collections for thread-safe buffers: `Collections.synchronizedList()`

### Companion Objects

- Use for constants and static loggers
- Define CSV headers as string arrays
- Define event type filters as lists
- Initialize loggers with class reference

**Example:**

```kotlin
companion object {
    private val LOGGER = LoggerFactory.getLogger(KpiListingGenerator::class.java)
    private val HEADERS = arrayOf("time", "correlation_id", "tenant_id", ...)
    private val EVENTS = listOf(TrackEvent.IMPRESSION, TrackEvent.VIEW, TrackEvent.CLICK)
}
```

## Architecture

### High-Level System Flow

```
RabbitMQ Queue → TrackingConsumer → Pipeline (Filters) → PersisterFilter → Cloud Storage (S3/Local)
                                                              ↓
                                                      Scheduled Jobs
                                                              ↓
                                            KpiListingGenerator → KPI CSV Files
```

### Package Structure

```
com.wutsi.koki.tracking.server/
├── Application.kt                      # Spring Boot application entry point
├── config/                             # Spring configuration
│   ├── AbstractRabbitMQConsumerConfiguration.kt
│   ├── PipelineConfiguration.kt        # Pipeline filter chain configuration
│   └── TrackingMQConfiguration.kt      # RabbitMQ consumer setup
├── dao/                                # Data access objects
│   ├── KpiListingRepository.kt         # KPI CSV storage
│   └── TrackRepository.kt              # Track event CSV storage
├── domain/                             # Domain entities
│   ├── KpiListingEntity.kt             # KPI aggregation entity
│   └── TrackEntity.kt                  # Tracking event entity
├── job/                                # Scheduled jobs
│   ├── KpiListingGeneratorJob.kt       # Daily KPI generation job
│   └── PersisterJob.kt                 # Periodic buffer flush job
└── service/                            # Business logic
    ├── Filter.kt                       # Filter interface
    ├── KpiListingGenerator.kt          # KPI generation service
    ├── Pipeline.kt                     # Filter chain executor
    ├── TrackingConsumer.kt             # RabbitMQ event consumer
    └── filter/                         # Filter implementations
        ├── BotFilter.kt                # Bot detection filter
        ├── CountryFilter.kt            # IP-to-country resolution
        ├── DeviceTypeFilter.kt         # Device classification
        ├── PersisterFilter.kt          # Buffered CSV persistence
        └── SourceFilter.kt             # Traffic source attribution
```

### Component Responsibilities

#### TrackingConsumer

- Consumes `TrackSubmittedEvent` from RabbitMQ
- Converts DTO to `TrackEntity`
- Handles multi-product events (pipe-separated product IDs)
- Logs structured tracking context via `KVLogger`
- Delegates to pipeline for processing

#### Pipeline

- Executes filters in sequence
- Passes `TrackEntity` through each filter
- Returns enriched entity
- Order matters: PersisterFilter must be last

#### Filters

Each filter enriches tracking data:

- **BotFilter**: Detects bots using UAParser (device family = "spider")
- **CountryFilter**: Resolves IP to country code via GeoIP service (with caching)
- **DeviceTypeFilter**: Classifies device as DESKTOP, MOBILE, TABLET, or UNKNOWN
- **SourceFilter**: Detects traffic source (direct, social, search, email, etc.)
- **PersisterFilter**: Buffers events and flushes to CSV storage when buffer is full

#### TrackRepository

- Reads/writes tracking events in CSV format
- Defines CSV schema with 23 columns
- Uses Apache Commons CSV for parsing/printing
- Stores files to cloud storage via `StorageService`
- Organizes files by date: `track/yyyy/MM/dd/`

#### KpiListingRepository

- Writes KPI aggregations in CSV format
- Defines CSV schema with 7 columns
- Stores monthly KPI files: `kpi/yyyy/MM/listings.csv`

#### KpiListingGenerator

- Loads all tracking events for a given month
- Filters out bot traffic and events without product IDs
- Groups by product ID and calculates metrics:
    - Total impressions
    - Total views
    - Total clicks
    - Total messages
    - Total unique visitors (by device ID)
- Stores aggregated KPIs to CSV

#### Jobs

- **PersisterJob**: Flushes buffer on schedule and pre-destroy
- **KpiListingGeneratorJob**: Generates monthly KPIs on schedule

### Filter Chain Pattern

The pipeline uses the **Chain of Responsibility** pattern:

1. Each filter implements `Filter` interface
2. `Pipeline` maintains ordered list of filters
3. Each filter receives a `TrackEntity` and returns modified entity
4. Filters are stateless (except `PersisterFilter` which buffers)
5. Order is configured in `PipelineConfiguration`

**Critical Order:**

```kotlin
Pipeline(
    listOf(
        botFilter,          // Detect bots first
        countryFilter,      // Geo-location
        deviceTypeFilter,   // Device classification
        sourceFilter,       // Traffic source
        persisterFilter     // MUST BE LAST - persists to storage
    )
)
```

### RabbitMQ Integration

- Uses `AbstractRabbitMQConsumerConfiguration` base class
- Sets up exchange, queue, and DLQ (dead letter queue)
- Delayed consumer registration to avoid Spring initialization race conditions
- Scheduled DLQ processing for retry logic
- Manual acknowledgment mode (`autoAck = false`)

### Storage Strategy

#### Track Events

- **Buffering**: Events buffered in memory until buffer size reached
- **Format**: CSV with 23 columns
- **Organization**: `track/yyyy/MM/dd/UUID.csv`
- **Flushing**: On buffer full, scheduled cron, or application shutdown

#### KPI Files

- **Format**: CSV with 7 columns
- **Organization**: `kpi/yyyy/MM/listings.csv`
- **Generation**: Monthly aggregation of all track events
- **Overwrite**: Each generation replaces previous month's file

### Caching Strategy

- **Redis** caching for GeoIP lookups (configured in koki-platform)
- **UAParser** instances cached at filter level (single instance)
- **TrafficSourceDetector** cached at filter level (single instance)

### Multi-Product Event Handling

Tracking events can contain multiple product IDs (pipe-separated):

```kotlin
productId = "1234|5678|9012"
```

The consumer splits these into separate `TrackEntity` instances:

```kotlin
val productIds = entity.productId.split("|")
return productIds.map { productId ->
    entity.copy(
        productId = productId,
        rank = if (track.event == TrackEvent.IMPRESSION) index++ else track.rank
    )
}
```

## Testing Guidelines

### Testing Philosophy

- **High coverage required**: 97% line coverage, 91% class coverage (enforced by JaCoCo)
- **Unit tests for business logic**: All filters, services, and generators
- **Integration tests for persistence**: Repository tests with real CSV I/O
- **Mock external dependencies**: RabbitMQ, storage service, GeoIP service

### Test Framework Setup

- **JUnit 5** for test structure
- **Mockito-Kotlin** for mocking (`com.nhaarman.mockitokotlin2`)
- **Kotlin Test** for assertions (`kotlin.test`)
- **Spring Boot Test** for integration tests

### Test Class Structure

#### Unit Tests (Service/Filter Tests)

```kotlin
internal class BotFilterTest {
    private val filter = BotFilter(DefaultKVLogger())

    @Test
    fun web() {
        val track = createTrack("Mozilla/5.0 ...")
        assertFalse(filter.filter(track).bot)
    }

    @Test
    fun bot() {
        val track = createTrack("Googlebot/2.1 ...")
        assertTrue(filter.filter(track).bot)
    }

    private fun createTrack(ua: String?) = TrackEntity(ua = ua, bot = false)
}
```

#### Integration Tests (Repository Tests)

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrackRepositoryTest {
    @Autowired
    private lateinit var dao: TrackRepository

    @MockitoBean
    private lateinit var storageServiceBuilder: StorageServiceBuilder

    private lateinit var storage: StorageService

    @BeforeEach
    fun setUp() {
        storage = LocalStorageService(directory, baseUrl)
        doReturn(storage).whenever(storageServiceBuilder).default()
    }

    @Test
    fun save() {
        val date = LocalDate.now(ZoneId.of("UTC"))
        val url = dao.save(date, arrayListOf(createTrack()))

        val out = ByteArrayOutputStream()
        storage.get(url, out)
        assertEquals("...", out.toString().trimIndent())
    }
}
```

#### Tests with Mocks

```kotlin
class TrackingConsumerTest {
    private val pipeline = mock<Pipeline>()
    private val logger = DefaultKVLogger()
    private val consumer = TrackingConsumer(pipeline, logger)

    @Test
    fun `tracking event submitted`() {
        val track = createTrack()
        consumer.consume(TrackSubmittedEvent(track = track))

        val entity = argumentCaptor<TrackEntity>()
        verify(pipeline).filter(entity.capture())

        assertEquals(track.time, entity.firstValue.time)
        assertEquals(track.ua, entity.firstValue.ua)
    }
}
```

### Testing Patterns

#### Mocking with Mockito-Kotlin

```kotlin
private val dao = mock<TrackRepository>()
private val filter = PersisterFilter(dao, BUFFER_SIZE)

// Verify method called
verify(dao).save(any(), any())

// Verify method NOT called
verify(dao, never()).save(any(), any())

// Capture arguments
val items = argumentCaptor<List<TrackEntity>>()
verify(dao).save(any(), items.capture())
assertTrue(items.firstValue.contains(track))
```

#### Creating Test Data

```kotlin
private fun createTrack(correlationId: String = "123") = TrackEntity(
    time = 3333,
    correlationId = correlationId,
    tenantId = 1,
    deviceId = "sample-device",
    accountId = "333",
    productId = "1234",
    page = "SR",
    event = TrackEvent.VIEW,
)
```

#### Testing CSV Parsing

```kotlin
@Test
fun read() {
    val csv = """
        time,correlation_id,tenant_id,...
        3333,123,1,...
    """.trimIndent()

    val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

    assertEquals(1, tracks.size)
    assertEquals(3333L, tracks[0].time)
    assertEquals("123", tracks[0].correlationId)
}
```

#### Testing Buffer Logic

```kotlin
@Test
fun `cache track`() {
    for (i in 1..BUFFER_SIZE - 1) {
        filter.filter(createTrack(i.toString()))
    }

    verify(dao, never()).save(any(), any())
    assertEquals(4, filter.size())
}

@Test
fun `store track`() {
    for (i in 1..BUFFER_SIZE + 2) {
        filter.filter(createTrack(i.toString()))
    }

    val items = argumentCaptor<List<TrackEntity>>()
    verify(dao).save(any(), items.capture())
    assertEquals(BUFFER_SIZE, items.firstValue.size)
}
```

### Test Coverage Goals

- **Line coverage**: 97% (enforced by JaCoCo)
- **Class coverage**: 91% (enforced by JaCoCo)
- Focus on business logic: filters, services, generators
- Integration tests for CSV I/O and storage

### Test Data Location

- CSV fixtures in `src/test/resources/`
- Use `trimIndent()` for inline CSV data
- Use `ByteArrayInputStream` and `ByteArrayOutputStream` for CSV testing

## Documentation Guidelines

### Class Documentation

- Add KDoc comments for:
    - Service classes with complex logic
    - Filters explaining what they enrich
    - Repositories explaining storage format
    - Jobs explaining schedule and purpose

**Example:**

```kotlin
/**
 * Detects and flags bot traffic using UAParser library.
 *
 * Identifies bots by checking if device family equals "spider".
 * Returns original track entity unchanged if user-agent is null.
 */
@Service
class BotFilter(private val logger: KVLogger) : Filter {
    // ...
}
```

### Method Documentation

- Document public methods with non-obvious behavior
- Document complex algorithms (e.g., multi-product splitting)
- Omit documentation for self-explanatory methods (e.g., `filter()`, `flush()`)

### Configuration Documentation

- Document all configuration properties in `application.yml`
- Explain cron expressions
- Document buffer sizes and thresholds

### README.md

Must include:

- Purpose and features (comprehensive list)
- Tech stack with badges
- High-level architecture diagram
- Repository structure
- Usage examples (if applicable)
- License information

## Behavior

### Event Processing

- **Asynchronous**: Events processed asynchronously via `@EnableAsync`
- **Non-blocking**: Consumer returns quickly, processing happens in background
- **Ordered**: Filters execute in sequence (order matters)
- **Graceful degradation**: Filters return original entity on error

### Data Persistence

- **Buffered writes**: Events buffered in memory before flushing
- **Batch operations**: Multiple events written in single CSV file
- **Atomic writes**: Write to temp file, then upload to cloud
- **Cleanup**: Temp files deleted after upload

### Error Handling

- **DLQ for message failures**: Failed messages routed to dead letter queue
- **Scheduled retry**: DLQ processed on schedule for retry
- **Filter failures**: Non-critical - log warning and continue
- **Critical failures**: Let exceptions propagate for visibility

### Scheduled Jobs

- **PersisterJob**: Runs on cron schedule to flush buffer
- **KpiListingGeneratorJob**: Runs monthly to generate KPI aggregations
- **DLQ Processing**: Runs on cron schedule to retry failed messages

### Scalability

- **Stateless filters**: All filters are stateless (except PersisterFilter buffer)
- **Horizontal scaling**: Multiple consumer instances can run in parallel
- **Synchronized buffer**: Buffer is thread-safe using `Collections.synchronizedList()`
- **Per-instance buffers**: Each instance has its own buffer

### Cache Behavior

- **GeoIP caching**: Redis cache for country lookups (improves performance)
- **UAParser instances**: Single instance per filter (thread-safe)
- **Cache expiration**: Managed by koki-platform configuration

### Storage Organization

#### Daily Tracking Events

```
track/
  2025/
    11/
      20/
        abc123.csv
        def456.csv
      21/
        ghi789.csv
```

#### Monthly KPI Files

```
kpi/
  2025/
    11/
      listings.csv
```

### Configuration Properties

All properties defined in `application.yml`:

- `koki.persister.buffer-size`: Number of events to buffer before flush
- `koki.persister.cron`: Cron expression for buffer flush schedule
- `koki.module.tracking.mq.queue`: RabbitMQ queue name
- `koki.module.tracking.mq.dlq`: Dead letter queue name
- `koki.module.tracking.mq.consumer-delay-seconds`: Delay before consumer registration
- `koki.module.tracking.mq.dlq-cron`: Cron expression for DLQ processing

### Application Lifecycle

#### Startup

1. Spring Boot initializes beans
2. RabbitMQ consumer registration delayed (via Timer)
3. Pipeline configured with ordered filters
4. Scheduled jobs registered

#### Runtime

1. Events arrive in RabbitMQ queue
2. Consumer converts to `TrackEntity`
3. Pipeline enriches through filters
4. PersisterFilter buffers events
5. Periodic flush to cloud storage
6. Scheduled KPI generation

#### Shutdown

1. `@PreDestroy` triggered on PersisterJob
2. Buffer flushed to ensure no data loss
3. Application terminates

### Thread Safety

- **Buffer**: Synchronized list for thread-safe access
- **Filters**: Stateless design (except PersisterFilter)
- **UAParser**: Thread-safe parser instances
- **Storage**: Operations are atomic (temp file → cloud upload)

### Cloud Storage Integration

- **Abstraction**: Uses `StorageService` interface from koki-platform
- **Local storage**: For development and testing
- **AWS S3**: For production deployment
- **URL-based access**: All files accessible via URL

### Dependency Injection

- **Constructor injection**: All dependencies injected via constructor
- **No field injection**: No `@Autowired` on fields
- **Bean configuration**: Explicit `@Bean` methods in configuration classes
- **Spring Boot auto-configuration**: Leverages Spring Boot defaults

### Security

- **Spring Security**: Basic authentication enabled
- **Actuator endpoints**: Exposed for monitoring (health, info, metrics)
- **RabbitMQ credentials**: Configured via environment variables
- **AWS credentials**: Configured via environment variables or IAM roles

### Monitoring and Observability

- **Spring Boot Actuator**: Health, info, metrics endpoints
- **Structured logging**: KVLogger for tracking context
- **SLF4J logging**: Operational messages
- **JaCoCo coverage**: Code coverage metrics
- **GitHub Actions**: CI/CD with automated testing

### Heroku Deployment

- **Procfile**: Defines web process
- **system.properties**: Specifies Java version (17)
- **Environment variables**: Configuration via Heroku config vars
- **Add-ons**: RabbitMQ (CloudAMQP), Redis (Heroku Redis)

