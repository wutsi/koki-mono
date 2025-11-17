# koki-platform

Shared infrastructure library providing reusable, production-ready abstractions for cross-cutting concerns across the
Koki platform, including storage, caching, messaging, multi-tenancy, and AI integration.

[![koki-platform-master](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml)
[![koki-platform-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml)
[![Code Coverage](../../.github/badges/koki-platform-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml)

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [Module Reference](#module-reference)
- [Usage Examples](#usage-examples)
- [License](#license)

## Features

- **Storage Abstraction**: Unified interface for local filesystem and AWS S3 storage with tenant-aware path management
  and pluggable providers
- **Distributed Caching**: High-performance Redis and local in-memory caching with Spring Cache abstraction and
  configurable TTL
- **Message Queue Integration**: RabbitMQ messaging with durable queues, dead-letter queue support, automatic retry
  logic, and Mustache templating
- **Email Messaging**: SMTP email service with HTML/plain text support, Mustache template rendering, and multi-recipient
  capabilities
- **Multi-Tenancy Support**: Comprehensive tenant isolation with context resolution, tenant-aware storage paths, cache
  keys, and automatic header injection
- **Security Integration**: JWT token parsing and validation with Spring Security integration and access token holder
  for request context
- **Structured Logging**: Key-value logging with automatic tenant ID, trace ID propagation, and request/response logging
- **Template Engine**: Mustache-based dynamic content generation with variable substitution, conditional logic, and
  reusable fragments
- **Translation Services**: AWS Translate integration with automatic language detection, translation caching, and batch
  processing
- **AI Provider Abstraction**: Pluggable AI/LLM integration with Google Gemini provider implementation and extensible
  interfaces
- **GeoIP Services**: IP geolocation capabilities for country, city, ISP, and organization detection
- **Async Execution**: Thread pool management with configurable pools, async task execution, and MDC context propagation
- **Tracking Integration**: Event tracking and analytics with automatic context enrichment and async event processing
- **Hibernate Type Extensions**: Custom Hibernate types for JSON columns, array columns, and custom type mappings
- **URL Utilities**: URL manipulation and validation with parsing, building, query parameter handling, and
  encoding/decoding
- **Debug Tools**: Development and troubleshooting utilities with REST request/response logging, performance profiling,
  and configuration inspection

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple?logo=kotlin) ![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Frameworks

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green?logo=springsecurity) ![Hibernate](https://img.shields.io/badge/Hibernate-6.x-orange?logo=hibernate)

### Infrastructure

![AWS S3](https://img.shields.io/badge/AWS%20S3-Storage-orange?logo=amazons3) ![AWS Translate](https://img.shields.io/badge/AWS%20Translate-AI-orange?logo=amazonaws) ![Redis](https://img.shields.io/badge/Redis-Cache-red?logo=redis) ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.2-orange?logo=rabbitmq)

### AI & ML

![Google Gemini](https://img.shields.io/badge/Google%20Gemini-AI-blue?logo=google)

### Tools & Libraries

![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven) ![Mustache](https://img.shields.io/badge/Mustache-Templates-yellow) ![Lettuce](https://img.shields.io/badge/Lettuce-Redis%20Client-red) ![Apache Commons](https://img.shields.io/badge/Apache%20Commons-Utilities-red?logo=apache) ![PDFBox](https://img.shields.io/badge/PDFBox-PDF-red?logo=apache) ![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5)

## High-Level Architecture

### Repository Structure

```
koki-platform/
├── pom.xml                          # Maven project configuration
├── README.md                        # This documentation
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/wutsi/koki/platform/
│   │   │       ├── KokiApplication.kt       # Main annotation for enabling platform
│   │   │       ├── ai/                      # AI provider abstraction
│   │   │       │   ├── AIProvider.kt        # AI provider interface
│   │   │       │   ├── AIProviderType.kt    # Provider type enum
│   │   │       │   ├── gemini/              # Google Gemini implementation
│   │   │       │   └── config/              # AI configuration
│   │   │       ├── cache/                   # Caching abstraction
│   │   │       │   ├── CacheService.kt      # Cache interface
│   │   │       │   ├── local/               # Local cache implementation
│   │   │       │   ├── redis/               # Redis cache implementation
│   │   │       │   └── config/              # Cache configuration
│   │   │       ├── debug/                   # Debug utilities
│   │   │       │   └── DebugRestInterceptor.kt
│   │   │       ├── executor/                # Async execution
│   │   │       │   └── config/              # Thread pool configuration
│   │   │       ├── geoip/                   # GeoIP services
│   │   │       │   ├── GeoIPService.kt      # GeoIP interface
│   │   │       │   └── impl/                # GeoIP implementations
│   │   │       ├── logger/                  # Structured logging
│   │   │       │   ├── KVLogger.kt          # Key-value logger interface
│   │   │       │   ├── DefaultKVLogger.kt   # Default implementation
│   │   │       │   ├── servlet/             # Servlet filter integration
│   │   │       │   └── config/              # Logger configuration
│   │   │       ├── messaging/               # Email messaging
│   │   │       │   ├── MessagingService.kt  # Messaging interface
│   │   │       │   ├── smtp/                # SMTP implementation
│   │   │       │   └── config/              # Messaging configuration
│   │   │       ├── mq/                      # Message queue (RabbitMQ)
│   │   │       │   ├── MessageQueue.kt      # MQ interface
│   │   │       │   ├── rabbitmq/            # RabbitMQ implementation
│   │   │       │   └── config/              # MQ configuration
│   │   │       ├── security/                # Security integration
│   │   │       │   ├── AccessTokenHolder.kt # Token holder interface
│   │   │       │   ├── JWTDecoder.kt        # JWT decoder
│   │   │       │   └── config/              # Security configuration
│   │   │       ├── storage/                 # File storage abstraction
│   │   │       │   ├── StorageService.kt    # Storage interface
│   │   │       │   ├── local/               # Local filesystem storage
│   │   │       │   ├── s3/                  # AWS S3 storage
│   │   │       │   └── config/              # Storage configuration
│   │   │       ├── templating/              # Template engine
│   │   │       │   ├── TemplatingEngine.kt  # Template interface
│   │   │       │   └── MustacheTemplatingEngine.kt
│   │   │       ├── tenant/                  # Multi-tenancy
│   │   │       │   ├── TenantProvider.kt    # Tenant provider interface
│   │   │       │   ├── TenantHolder.kt      # Tenant holder
│   │   │       │   ├── filter/              # Tenant resolution filters
│   │   │       │   └── config/              # Tenant configuration
│   │   │       ├── tracing/                 # Distributed tracing
│   │   │       │   ├── TracingContext.kt    # Trace context
│   │   │       │   └── config/              # Tracing configuration
│   │   │       ├── tracking/                # Event tracking
│   │   │       │   ├── TrackingService.kt   # Tracking interface
│   │   │       │   └── config/              # Tracking configuration
│   │   │       ├── translation/             # Translation services
│   │   │       │   ├── TranslationService.kt # Translation interface
│   │   │       │   ├── aws/                 # AWS Translate implementation
│   │   │       │   └── config/              # Translation configuration
│   │   │       ├── url/                     # URL utilities
│   │   │       │   └── URLBuilder.kt        # URL building utilities
│   │   │       └── util/                    # General utilities
│   │   │           ├── DateUtil.kt          # Date utilities
│   │   │           ├── StringUtil.kt        # String utilities
│   │   │           └── CustomPhysicalNamingStrategy.kt
│   │   └── resources/
│   │       └── application.yml              # Default configuration
│   └── test/
│       └── kotlin/
│           └── com/wutsi/koki/platform/     # Unit tests (61 test files)
└── target/                                  # Build output directory
```

**Key Components:**

- **@KokiApplication**: Main annotation to enable all platform features in a Spring Boot application
- **Provider Interfaces**: Abstract interfaces for all infrastructure services (storage, cache, messaging, AI, etc.)
- **Provider Implementations**: Concrete implementations for different environments (local vs. cloud, dev vs. prod)
- **Configuration Classes**: Spring Boot auto-configuration classes that wire up providers based on application.yml
- **Utilities**: Helper classes for common tasks (logging, URL building, date handling, etc.)

### Module Reference

| Module          | Description                    | Key Classes                                                 |
|-----------------|--------------------------------|-------------------------------------------------------------|
| **ai**          | AI/LLM provider abstraction    | `AIProvider`, `GeminiAIProvider`                            |
| **cache**       | Distributed caching            | `CacheService`, `RedisCacheService`, `LocalCacheService`    |
| **debug**       | Development debugging tools    | `DebugRestInterceptor`                                      |
| **executor**    | Async task execution           | `ExecutorConfiguration`                                     |
| **geoip**       | IP geolocation services        | `GeoIPService`                                              |
| **logger**      | Structured logging             | `KVLogger`, `DefaultKVLogger`, `KVLoggerFilter`             |
| **messaging**   | Email messaging                | `MessagingService`, `SMTPMessagingService`                  |
| **mq**          | Message queue (RabbitMQ)       | `MessageQueue`, `RabbitMQMessageQueue`                      |
| **security**    | Authentication & authorization | `AccessTokenHolder`, `JWTDecoder`                           |
| **storage**     | File storage abstraction       | `StorageService`, `LocalStorageService`, `S3StorageService` |
| **templating**  | Template rendering             | `TemplatingEngine`, `MustacheTemplatingEngine`              |
| **tenant**      | Multi-tenancy support          | `TenantProvider`, `TenantHolder`, `TenantFilter`            |
| **tracing**     | Distributed tracing            | `TracingContext`                                            |
| **tracking**    | Event tracking & analytics     | `TrackingService`                                           |
| **translation** | Multi-language translation     | `TranslationService`, `AWSTranslationService`               |
| **url**         | URL manipulation               | `URLBuilder`                                                |
| **util**        | General utilities              | `DateUtil`, `StringUtil`, `CustomPhysicalNamingStrategy`    |

## Usage Examples

### Storage Service

```kotlin
import com.wutsi.koki.platform.storage.StorageService
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream

@Service
class FileUploadService(private val storage: StorageService) {

    fun uploadFile(tenantId: Long, filename: String, content: ByteArray): String {
        val path = "tenants/$tenantId/files/$filename"
        val contentType = "application/pdf"

        val url = storage.store(
            path = path,
            content = ByteArrayInputStream(content),
            contentType = contentType
        )

        return url
    }

    fun downloadFile(path: String): ByteArray {
        return storage.get(path).readBytes()
    }
}
```

### Cache Service

```kotlin
import com.wutsi.koki.platform.cache.CacheService
import org.springframework.stereotype.Service

@Service
class UserCacheService(private val cache: CacheService) {

    fun getUser(userId: Long): User? {
        val cacheKey = "user:$userId"
        return cache.get(cacheKey, User::class.java)
    }

    fun cacheUser(user: User) {
        val cacheKey = "user:${user.id}"
        cache.put(cacheKey, user, ttl = 3600) // 1 hour TTL
    }

    fun evictUser(userId: Long) {
        val cacheKey = "user:$userId"
        cache.delete(cacheKey)
    }
}
```

### Message Queue

```kotlin
import com.wutsi.koki.platform.mq.MessageQueue
import org.springframework.stereotype.Service

@Service
class NotificationService(private val mq: MessageQueue) {

    fun sendNotification(userId: Long, message: String) {
        mq.publish(
            queue = "notifications",
            message = mapOf(
                "userId" to userId,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )
        )
    }

    fun processNotifications() {
        mq.consume(queue = "notifications") { message ->
            val userId = message["userId"] as Long
            val text = message["message"] as String
            // Process notification
            println("Sending notification to user $userId: $text")
        }
    }
}
```

### Structured Logging

```kotlin
import com.wutsi.koki.platform.logger.KVLogger
import org.springframework.stereotype.Service

@Service
class OrderService(private val logger: KVLogger) {

    fun createOrder(order: Order): Long {
        logger.add("order_id", order.id)
        logger.add("amount", order.amount)
        logger.add("currency", order.currency)

        try {
            // Business logic
            val orderId = saveOrder(order)

            logger.add("status", "success")
            logger.add("result_order_id", orderId)

            return orderId
        } catch (ex: Exception) {
            logger.add("status", "error")
            logger.add("error_message", ex.message)
            logger.setException(ex)
            throw ex
        }
    }
}
```

### AI Provider

```kotlin
import com.wutsi.koki.platform.ai.AIProvider
import org.springframework.stereotype.Service

@Service
class ContentGenerationService(private val ai: AIProvider) {

    fun generatePropertyDescription(property: Property): String {
        val prompt = """
            Generate a compelling property description for:
            - Type: ${property.type}
            - Bedrooms: ${property.bedrooms}
            - Bathrooms: ${property.bathrooms}
            - Location: ${property.location}
            - Price: ${property.price}
        """.trimIndent()

        return ai.generate(prompt, maxTokens = 200)
    }
}
```

### Translation Service

```kotlin
import com.wutsi.koki.platform.translation.TranslationService
import org.springframework.stereotype.Service

@Service
class ContentTranslationService(private val translation: TranslationService) {

    fun translateListing(text: String, targetLanguage: String): String {
        return translation.translate(
            text = text,
            sourceLang = "en",
            targetLang = targetLanguage
        )
    }

    fun translateBatch(texts: List<String>, targetLanguage: String): List<String> {
        return texts.map { text ->
            translation.translate(text, "en", targetLanguage)
        }
    }
}
```

### Template Engine

```kotlin
import com.wutsi.koki.platform.templating.TemplatingEngine
import org.springframework.stereotype.Service

@Service
class EmailTemplateService(private val templating: TemplatingEngine) {

    fun renderWelcomeEmail(user: User): String {
        val template = """
            Hello {{name}},

            Welcome to Koki! Your account has been created successfully.

            Username: {{username}}
            Email: {{email}}
        """.trimIndent()

        return templating.render(
            template = template,
            data = mapOf(
                "name" to user.name,
                "username" to user.username,
                "email" to user.email
            )
        )
    }
}
```

### Multi-Tenancy

```kotlin
import com.wutsi.koki.platform.tenant.TenantProvider
import org.springframework.stereotype.Service

@Service
class TenantAwareService(private val tenantProvider: TenantProvider) {

    fun getCurrentTenant(): Long {
        return tenantProvider.id()
    }

    fun getTenantName(): String {
        return tenantProvider.name()
    }

    fun withTenant(tenantId: Long, action: () -> Unit) {
        val currentTenant = tenantProvider.id()
        try {
            tenantProvider.set(tenantId)
            action()
        } finally {
            tenantProvider.set(currentTenant)
        }
    }
}
```

## License

This project is licensed under the MIT License. See the [LICENSE](../../LICENSE.md) file for details.

