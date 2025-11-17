# Setup Guide - koki-platform

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)

## Prerequisites

The koki-platform is a library module consumed by other Koki services. To work with it directly:

- **Java Development Kit (JDK) 17** or higher
    - Verify: `java -version`
    - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

- **Maven 3.8+** for dependency management and build
    - Verify: `mvn -version`
    - Download: [Apache Maven](https://maven.apache.org/download.cgi)

- **Spring Boot 3.5.7+** (for consuming services)

### Optional Dependencies (for specific features)

- **Redis 7.x+** (for distributed caching)
    - Verify: `redis-cli ping`
    - Download: [Redis](https://redis.io/download/)

- **RabbitMQ 4.x+** (for message queue integration)
    - Verify: `rabbitmqctl status`
    - Download: [RabbitMQ](https://www.rabbitmq.com/download.html)

- **AWS Account** (for S3 storage and AWS Translate services)
    - Configure AWS CLI with credentials

- **Google Gemini API Key** (for AI integration)
    - Obtain from: [Google AI Studio](https://makersuite.google.com/app/apikey)

## Installation

### As a Library Developer

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-platform
```

2. Build the library:

```bash
mvn clean install
```

The compiled JAR will be installed to your local Maven repository at:

```
~/.m2/repository/com/wutsi/koki/koki-platform/0.0.50-SNAPSHOT/
```

### As a Consumer Application

Add the dependency to your Maven project:

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-platform</artifactId>
    <version>0.0.50-SNAPSHOT</version>
</dependency>
```

For Gradle projects:

```gradle
implementation 'com.wutsi.koki:koki-platform:0.0.50-SNAPSHOT'
```

### GitHub Packages Authentication

Since koki-platform is published to GitHub Packages, configure authentication in your Maven `settings.xml`:

```xml

<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_PERSONAL_ACCESS_TOKEN</password>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/wutsi/koki-mono</url>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>
</settings>
```

## Database Setup

This is a library module and does **not** require a database. However, it provides Hibernate type extensions and
utilities that can be used by consuming applications that do use databases.

### Hibernate Custom Types

If your consuming application uses Hibernate, koki-platform provides custom types:

- JSON column mapping
- Array column mapping
- Custom physical naming strategy

No database setup is required for the library itself.

## Configuration

### Enable koki-platform in Your Spring Boot Application

Annotate your Spring Boot application class with `@KokiApplication`:

```kotlin
import com.wutsi.koki.platform.KokiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@KokiApplication
class MyApplication

fun main(args: Array<String>) {
    runApplication<MyApplication>(*args)
}
```

This single annotation enables all platform features.

### Storage Configuration

Configure storage provider in `application.yml`:

#### Local Storage (Development)

```yaml
wutsi:
    platform:
        storage:
            enabled: true
            type: local
            local:
                directory: ${user.home}/.koki/storage
                base-url: http://localhost:8080
                servlet-path: /storage
```

#### AWS S3 Storage (Production)

```yaml
wutsi:
    platform:
        storage:
            enabled: true
            type: s3
            s3:
                bucket: my-koki-bucket
                region: us-east-1
                access-key: ${AWS_ACCESS_KEY_ID}
                secret-key: ${AWS_SECRET_ACCESS_KEY}
```

Set AWS credentials via environment variables:

```bash
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
```

### Cache Configuration

#### Redis Cache (Production)

```yaml
wutsi:
    platform:
        cache:
            name: koki
            type: redis
            ttl: 3600  # seconds
            redis:
                url: redis://:password@localhost:6379
```

#### Local Cache (Development)

```yaml
wutsi:
    platform:
        cache:
            type: local
            ttl: 3600
```

### Message Queue Configuration

#### RabbitMQ

```yaml
wutsi:
    platform:
        mq:
            type: rabbitmq
            rabbitmq:
                url: amqp://guest:guest@localhost:5672
                exchange-name: koki
                max-retries: 3
                ttl-seconds: 3600
```

### Email Messaging Configuration

#### SMTP

```yaml
spring:
    mail:
        host: smtp.gmail.com
        port: 587
        username: your-email@gmail.com
        password: your-app-password
        properties:
            mail:
                smtp:
                    auth: true
                    starttls:
                        enable: true
```

### Translation Configuration

#### AWS Translate

```yaml
wutsi:
    platform:
        translation:
            type: aws
            aws:
                region: us-east-1
                access-key: ${AWS_ACCESS_KEY_ID}
                secret-key: ${AWS_SECRET_ACCESS_KEY}
```

### AI Configuration

#### Google Gemini

```yaml
wutsi:
    platform:
        ai:
            enabled: true
            type: gemini
            gemini:
                api-key: ${GEMINI_API_KEY}
                model: gemini-pro
```

Set API key via environment variable:

```bash
export GEMINI_API_KEY="your-gemini-api-key"
```

### Multi-Tenancy Configuration

```yaml
wutsi:
    platform:
        tenant:
            enabled: true
```

Tenant resolution is automatic via HTTP headers (`X-Tenant-ID`).

### Thread Pool Configuration

```yaml
wutsi:
    platform:
        executor:
            thread-pool:
                name: koki-platform
                size: 16
```

### Complete Configuration Example

Here's a complete `application.yml` example:

```yaml
wutsi:
    platform:
        # Storage
        storage:
            enabled: true
            type: local
            local:
                directory: ${user.home}/.koki/storage
                base-url: http://localhost:8080
                servlet-path: /storage

        # Cache
        cache:
            name: koki
            type: redis
            ttl: 3600
            redis:
                url: redis://:password@localhost:6379

        # Message Queue
        mq:
            type: rabbitmq
            rabbitmq:
                url: amqp://guest:guest@localhost:5672
                exchange-name: koki
                max-retries: 3
                ttl-seconds: 3600

        # Translation
        translation:
            type: aws
            aws:
                region: us-east-1

        # AI
        ai:
            enabled: true
            type: gemini
            gemini:
                api-key: ${GEMINI_API_KEY}
                model: gemini-pro

        # Tenant
        tenant:
            enabled: true

        # Thread Pool
        executor:
            thread-pool:
                name: koki-platform
                size: 16

# Spring Mail (for email messaging)
spring:
    mail:
        host: smtp.gmail.com
        port: 587
        username: ${SMTP_USERNAME}
        password: ${SMTP_PASSWORD}
        properties:
            mail:
                smtp:
                    auth: true
                    starttls:
                        enable: true
```

## Running the Project

### This is a Library Module

The koki-platform is a library and does not run independently. It is consumed as a dependency by other Koki services (
koki-server, koki-portal, etc.).

### Verify the Build

To verify the library builds correctly:

```bash
mvn clean verify
```

### Install Locally

To install the library to your local Maven repository for use by other modules:

```bash
mvn clean install
```

### Using the Library in Your Application

After adding the dependency and configuring your application:

1. Start your Spring Boot application
2. The platform features will be auto-configured based on your `application.yml`
3. Inject the services you need:

```kotlin
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.cache.CacheService
import com.wutsi.koki.platform.mq.MessageQueue
import org.springframework.stereotype.Service

@Service
class MyService(
    private val storage: StorageService,
    private val cache: CacheService,
    private val mq: MessageQueue
) {
    // Use the platform services
}
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=StorageServiceTest
```

### Run Tests Matching Pattern

```bash
mvn test -Dtest=*Storage*
```

### Full Build with Coverage

Run tests and generate coverage report:

```bash
mvn clean verify
```

This command:

1. Compiles the code
2. Runs all tests (61 test files)
3. Generates JaCoCo coverage report
4. Enforces thresholds (85% line coverage, 85% class coverage)

The build will fail if coverage is below the threshold.

### View Coverage Report

After running `mvn verify`, open the coverage report:

```bash
# macOS
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

### Skip Tests (Quick Build)

To build without running tests:

```bash
mvn clean package -DskipTests
```

### Integration Tests

Some tests require external services (Redis, RabbitMQ):

```bash
# Start Redis
docker run -d --name test-redis -p 6379:6379 redis:7-alpine

# Start RabbitMQ
docker run -d --name test-rabbit -p 5672:5672 rabbitmq:3

# Run tests
mvn verify

# Stop containers
docker stop test-redis test-rabbit
docker rm test-redis test-rabbit
```

### Test Configuration

Tests use in-memory/mock implementations where possible. For tests requiring real services:

- **Redis tests**: Use TestContainers or local Redis instance
- **RabbitMQ tests**: Use TestContainers or local RabbitMQ instance
- **S3 tests**: Use mock S3 client
- **Email tests**: Use GreenMail test server

## Troubleshooting

| Issue                        | Symptom                          | Resolution                                                   |
|------------------------------|----------------------------------|--------------------------------------------------------------|
| Build fails                  | Compilation errors               | Ensure JDK 17+ and Maven 3.8+ are installed                  |
| Dependency resolution fails  | Cannot download dependencies     | Configure GitHub Packages authentication in `settings.xml`   |
| Tests fail                   | Redis/RabbitMQ connection errors | Start required services or use mock implementations          |
| Low coverage                 | Build fails at verify            | Add tests or temporarily reduce thresholds (not recommended) |
| @KokiApplication not working | Features not auto-configured     | Ensure annotation is on main Spring Boot application class   |
| Storage service unavailable  | Bean not created                 | Check `wutsi.platform.storage.enabled=true` in configuration |

### Common Configuration Issues

#### Storage Not Working

Ensure storage is enabled and type is specified:

```yaml
wutsi:
    platform:
        storage:
            enabled: true
            type: local  # or s3
```

#### Cache Not Working

Verify cache type and connection:

```yaml
wutsi:
    platform:
        cache:
            type: redis  # or local
            redis:
                url: redis://localhost:6379
```

Test Redis connection:

```bash
redis-cli ping
```

#### Message Queue Not Working

Check RabbitMQ configuration and connection:

```yaml
wutsi:
    platform:
        mq:
            type: rabbitmq
            rabbitmq:
                url: amqp://localhost:5672
```

Test RabbitMQ:

```bash
rabbitmqctl status
```

### Logging

Enable debug logging for troubleshooting:

```yaml
logging:
    level:
        com.wutsi.koki.platform: DEBUG
```

## Next Steps

After setting up koki-platform:

1. **Review Module Reference**: Check the README for detailed module documentation
2. **Explore Usage Examples**: See practical examples for each service
3. **Configure Features**: Enable only the features you need
4. **Integrate with Your App**: Add the library to your Spring Boot application
5. **Test Integration**: Write tests for your service implementations
6. **Monitor Performance**: Use structured logging and tracking features
7. **Scale with Redis**: Enable Redis caching for production workloads
8. **Secure Credentials**: Use environment variables for sensitive configuration

## Additional Resources

- [README.md](README.md) - Full feature documentation and usage examples
- [koki-dto](../koki-dto/README.md) - Data Transfer Objects used by platform
- [koki-server](../koki-server/README.md) - Example consumer of koki-platform
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Redis Documentation](https://redis.io/documentation)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [AWS S3 Documentation](https://docs.aws.amazon.com/s3/)
- [Google Gemini API](https://ai.google.dev/docs)

