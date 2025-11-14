# koki-platform

A shared utility library providing cross-cutting concerns for Koki services including storage, messaging, caching,
security, templating, and tracing.

[![koki-platform CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml)

[![koki-platform CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml)

![Coverage](../../.github/badges/koki-platform-jacoco.svg)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

## About the Project

**koki-platform** is a foundational library that abstracts common infrastructure concerns for Koki applications. It
provides pluggable implementations for storage (local/S3), messaging (RabbitMQ), caching (Redis), translation (AWS
Translate), templating (Mustache), and observability (structured logging, tracing). By centralizing these cross-cutting
concerns, it ensures consistency, reduces duplication, and simplifies service development across the entire Koki
ecosystem.

The library solves the problem of duplicated infrastructure code across multiple microservices by providing
battle-tested, production-ready abstractions that can be easily integrated with minimal configuration. This allows
development teams to focus on business logic while maintaining consistency in how services interact with external
systems, handle multi-tenancy, manage security contexts, and produce structured observability data.

### Key Features

- **Pluggable Storage** – Unified interface for local filesystem and AWS S3 storage with transparent switching via
  configuration
- **Message Queue Abstraction** – RabbitMQ-based async messaging with consumer patterns, retry logic, and dead-letter
  queues
- **Caching Layer** – Redis integration with Spring Cache abstraction for distributed caching across service instances
- **AI Provider Framework** – Extensible interface for LLM integrations (OpenAI, Anthropic) with tenant-specific
  configuration
- **Security Utilities** – JWT decoding, tenant context providers, access token holders for multi-tenant authentication
- **Translation Services** – AWS Translate integration for multilingual content support
- **Templating Engine** – Mustache-based template rendering for emails, notifications, and dynamic content
- **Structured Logging** – Key-value logger with automatic tenant and trace context propagation
- **Geo-IP Resolution** – Location-based services and geolocation lookup utilities
- **Execution Framework** – Asynchronous task execution helpers and thread pool management

## Getting Started

Integrate the platform library into your Koki service to leverage shared infrastructure utilities.

### Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **Kotlin 2.1.0+**

### Installation

Add the following dependency to your project:

**Maven:**

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-platform</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

**Gradle (Kotlin DSL):**

```kotlin
dependencies {
    implementation("com.wutsi.koki:koki-platform:VERSION_NUMBER")
}
```

### Configure GitHub Packages

**Maven** (`pom.xml`):

```xml

<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/wutsi/koki-mono</url>
    </repository>
</repositories>
```

**Gradle** (`build.gradle.kts`):

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

### Authentication

Configure GitHub Packages authentication. Set environment variables:

```bash
export GITHUB_USER=your-github-username
export GITHUB_PASSWORD=your-personal-access-token
```

Update **~/.m2/settings.xml**:

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

## Usage

### Storage Service

Configure storage backend in your **application.yml**:

```yaml
wutsi:
    platform:
        storage:
            type: local  # or s3
            local:
                directory: /var/koki/storage
                base-url: http://localhost:8080
            s3:
                bucket: koki-prod
                region: us-east-1
```

Use the storage service:

```kotlin
import com.wutsi.koki.platform.storage.StorageService

@Service
class FileUploadService(
    private val storageService: StorageService
) {
    fun upload(path: String, content: ByteArray): String {
        return storageService.store(path, content)
    }

    fun download(path: String): ByteArray {
        return storageService.get(path)
    }
}
```

### Messaging Service

Configure RabbitMQ:

```yaml
wutsi:
    platform:
        mq:
            type: rabbitmq
            rabbitmq:
                url: amqp://localhost
                exchange-name: koki-events
                max-retries: 3
                ttl-seconds: 86400
```

Send messages:

```kotlin
import com.wutsi.koki.platform.messaging.MessagingService
import com.wutsi.koki.platform.messaging.Message
import com.wutsi.koki.platform.messaging.Party

@Service
class NotificationService(
    private val messagingService: MessagingService
) {
    fun sendEmail(to: String, subject: String, body: String) {
        val message = Message(
            from = Party("noreply@koki.example"),
            to = listOf(Party(to)),
            subject = subject,
            content = body,
            html = true
        )
        messagingService.send(message)
    }
}
```

### Cache Configuration

Configure Redis cache:

```yaml
wutsi:
    platform:
        cache:
            type: redis
            ttl: 3600
            redis:
                url: redis://localhost:6379
```

Use caching annotations:

```kotlin
import org.springframework.cache.annotation.Cacheable

@Service
class LocationService {
    @Cacheable("locations")
    fun findById(id: Long): Location? {
        // Expensive database query
        return repository.findById(id)
    }
}
```

### AI Provider Integration

Use the AI provider factory for LLM integration:

```kotlin
import com.wutsi.koki.platform.ai.AIProviderFactory

@Service
class ContentGenerator(
    private val aiProviderFactory: AIProviderFactory
) {
    fun generate(prompt: String): String {
        val config = AIProviderConfiguration(
            provider = "openai",
            apiKey = "sk-...",
            model = "gpt-4",
            temperature = 0.7
        )
        val provider = aiProviderFactory.create(config)
        return provider.generate(prompt)
    }
}
```

### Tenant Context

Access tenant information in multi-tenant scenarios:

```kotlin
import com.wutsi.koki.platform.tenant.TenantProvider

@Service
class AccountService(
    private val tenantProvider: TenantProvider,
    private val repository: AccountRepository
) {
    fun findAll(): List<Account> {
        val tenantId = tenantProvider.id()
        return repository.findByTenantId(tenantId)
    }
}
```

### Structured Logging

Use **KVLogger** for structured, machine-parsable logs:

```kotlin
import com.wutsi.koki.platform.logger.KVLogger

@Service
class OrderService(
    private val logger: KVLogger
) {
    fun createOrder(request: CreateOrderRequest) {
        logger.add("order_type", request.type)
        logger.add("amount", request.amount)
        logger.add("customer_id", request.customerId)

        // Process order...

        logger.info("Order created successfully")
    }
}
```

### Templating

Render dynamic templates using Mustache:

```kotlin
import com.wutsi.koki.platform.templating.TemplateEngine

@Service
class EmailTemplateService(
    private val templateEngine: TemplateEngine
) {
    fun renderWelcomeEmail(name: String): String {
        val template = "Hello {{name}}, welcome to Koki!"
        return templateEngine.apply(
            template = template,
            variables = mapOf("name" to name)
        )
    }
}
```

## Contributing

We welcome contributions! Please see our [Contributing Guide](../../CONTRIBUTING.md) for details on how to get started.

### Local Development

For instructions on setting up your local development environment, see [DEVELOP.md](../../DEVELOP.md).

### Testing

For information about running tests and our testing practices, see [TESTING.md](../../TESTING.md).

## License

See the root [License](../../LICENSE.md).

