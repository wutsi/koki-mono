# Setup Guide - koki-portal-public

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)

## Prerequisites

Before running the koki-portal-public application, ensure you have the following software installed:

- **Java Development Kit (JDK) 17** or higher
    - Verify: `java -version`
    - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

- **Maven 3.8+** for dependency management and build
    - Verify: `mvn -version`
    - Download: [Apache Maven](https://maven.apache.org/download.cgi)

- **koki-server** running locally or remotely (required for backend REST APIs)
    - Default base URL: `http://localhost:8080`

- **Redis 7.x+** (required for caching in production; optional for local development but enabled by default)
    - Verify: `redis-cli ping` (should return `PONG`)

- **RabbitMQ 4.x+** (optional for tracking/event publishing integration)
    - Verify: `rabbitmqctl status`

### Optional Tools

- **Docker** (to run Redis and RabbitMQ quickly)
- **AWS CLI** (if using S3 for static assets and storage)
- **Modern Web Browser** (Chrome, Firefox, Safari, Edge)

### Quick Start with Docker (Optional)

Run Redis and RabbitMQ via Docker:

```bash
# Redis
docker run -d --name koki-redis -p 6379:6379 redis:7-alpine redis-server --requirepass test

# RabbitMQ
docker run -d --name koki-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

RabbitMQ Management UI: http://localhost:15672 (credentials: guest / guest)

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono
```

### 2. Build Shared Modules

Build required dependencies first:

```bash
mvn clean install -DskipTests
```

This installs:

- `koki-dto`
- `koki-platform`
- `koki-sdk`

### 3. Build the Portal Module

```bash
cd modules/koki-portal-public
mvn clean package
```

Artifact created: `target/koki-portal-public-1.0.0-SNAPSHOT.jar`

## Database Setup

This module does **not** directly connect to a database. All dynamic data is obtained via REST APIs from `koki-server`.

If your local environment serves dynamic listing data, ensure:

- `koki-server` has its database configured and running
- The SDK base URL points to the running server (`koki.sdk.base-url`)

No migrations or local schema setup are required for this module.

## Configuration

Configuration is managed via Spring Boot `application.yml` and environment-specific profiles (`application-test.yml`,
`application-prod.yml`). For local development you can use the default `application.yml` or create
`application-local.yml`.

### Default Configuration (application.yml)

Key settings:

```yaml
server:
    port: 8082

koki:
    webapp:
        client-id: koki-portal-public
        base-url: http://localhost:8082
        asset-url: ""
    rest:
        connection-timeout: 15000
        read-timeout: 15000
    sdk:
        base-url: http://localhost:8080   # koki-server

wutsi:
    platform:
        cache:
            name: koki
            type: redis
            ttl: 86400
            redis:
                url: redis://:test@localhost:6379
        mq:
            type: rabbitmq
            rabbitmq:
                url: amqp://localhost
                exchange-name: koki-tracking
                max-retries: 24
                ttl-seconds: 84600
        storage:
            enabled: true
            type: local
            local:
                directory: ${user.home}/__wutsi
                base-url: http://localhost:8082
                servlet-path: /local-storage
            s3:
                bucket: koki-test
                region: us-east-1
                access-key: ""
                secret-key: ""
```

### Test Profile (application-test.yml)

Used in CI and automated tests:

```yaml
koki:
    webapp:
        asset-url: https://com-wutsi-koki-test.s3.amazonaws.com/static/koki-portal-public
        base-url: ${APP_URL}
    sdk:
        base-url: ${KOKI_SERVER_URL}
    storage:
        type: s3
        s3:
            bucket: com-wutsi-koki-test
            region: us-east-1
            access-key: ${AWS_ACCESS_KEY}
            secret-key: ${AWS_SECRET_KEY}

wutsi:
    platform:
        cache:
            redis:
                url: ${REDISCLOUD_URL}
        storage:
            type: s3
            s3:
                bucket: com-wutsi-koki-test
                region: us-east-1
                access-key: ${AWS_ACCESS_KEY}
                secret-key: ${AWS_SECRET_KEY}
        mq:
            rabbitmq:
                url: ${CLOUDAMQP_URL}
```

### Production Profile (application-prod.yml)

```yaml
koki:
    webapp:
        asset-url: https://com-wutsi-koki-prod.s3.amazonaws.com/static/koki-portal-public
        base-url: ${APP_URL}
    sdk:
        base-url: ${KOKI_SERVER_URL}

wutsi:
    platform:
        cache:
            redis:
                url: ${REDISCLOUD_URL}
        storage:
            type: s3
            s3:
                bucket: com-wutsi-koki-prod
                region: us-east-1
                access-key: ${AWS_ACCESS_KEY}
                secret-key: ${AWS_SECRET_KEY}
        mq:
            rabbitmq:
                url: ${CLOUDAMQP_URL}
```

### Environment Variables

Common environment variables for test/prod deployments:

```bash
export APP_URL="https://portal.example.com"
export KOKI_SERVER_URL="https://api.example.com"
export REDISCLOUD_URL="redis://:password@redis-host:6379"
export CLOUDAMQP_URL="amqp://user:pass@rabbitmq-host/vhost"
export AWS_ACCESS_KEY="YOUR_ACCESS_KEY"
export AWS_SECRET_KEY="YOUR_SECRET_KEY"
```

Local development overrides:

```bash
export SERVER_PORT=8082
export KOKI_SERVER_URL=http://localhost:8080
export REDIS_URL=redis://:test@localhost:6379
```

### Local Storage vs S3

By default, local development uses filesystem storage:

```yaml
wutsi:
    platform:
        storage:
            type: local
            local:
                directory: ${user.home}/__wutsi
```

To switch to S3 locally (requires credentials):

```yaml
wutsi:
    platform:
        storage:
            type: s3
            s3:
                bucket: koki-test
                region: us-east-1
                access-key: ${AWS_ACCESS_KEY}
                secret-key: ${AWS_SECRET_KEY}
```

## Running the Project

### Run via Maven

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

If you don't create `application-local.yml`, the default `application.yml` will be used.

### Run Packaged JAR

```bash
mvn clean package
java -jar -Dspring.profiles.active=local target/koki-portal-public-1.0.0-SNAPSHOT.jar
```

### Verify Application Startup

Check health endpoint:

```bash
curl http://localhost:8082/actuator/health
```

Expected response fragment:

```json
{
    "status": "UP"
}
```

View info (includes git commit metadata if plugin ran):

```bash
curl http://localhost:8082/actuator/info
```

### Access the Portal

Open in browser:

```
http://localhost:8082/
```

Static assets served under `public/` and dynamic listing pages rendered via Thymeleaf templates in `templates/`.

### Using Local Storage for Uploaded Assets

Uploaded or generated content stored in:

```
${HOME}/__wutsi
```

### Enable Redis Locally (Optional)

If Redis is not running but referenced in configuration, either:

- Start Redis (recommended)
- Or change `wutsi.platform.cache.type` to `none` (if supported in platform code) [REPLACE IF DIFFERENT]

### Connecting to koki-server

Ensure `koki.sdk.base-url` points to a running instance of koki-server. For local development:

```yaml
koki:
    sdk:
        base-url: http://localhost:8080
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=HomePageControllerTest
```

### Run Tests Matching Pattern

```bash
mvn test -Dtest=*Controller*
```

### Full Build with Coverage Enforcement

```bash
mvn clean verify
```

This will:

1. Compile code
2. Run tests
3. Generate JaCoCo coverage report
4. Enforce thresholds (line >= 86%, class >= 86%)

Coverage report: `target/site/jacoco/index.html`

Open coverage report:

```bash
# macOS
open target/site/jacoco/index.html
# Linux
xdg-open target/site/jacoco/index.html
# Windows
start target/site/jacoco/index.html
```

### Skip Tests (Fast Build)

```bash
mvn clean package -DskipTests
```

### Troubleshooting Test Failures

- Ensure `koki-server` dependency endpoints are mocked or running
- Verify environment variables for test profile (e.g., `APP_URL`, `KOKI_SERVER_URL`)
- Selenium tests may require browser driver configuration [REPLACE IF SELENIUM USED]

## Troubleshooting

| Issue                   | Symptom                                | Resolution                                                                |
|-------------------------|----------------------------------------|---------------------------------------------------------------------------|
| Redis connection failed | `Connection refused`                   | Start Redis container or adjust cache config                              |
| RabbitMQ not reachable  | Timeouts                               | Confirm RabbitMQ running or remove MQ usage locally                       |
| SDK calls failing       | `Connection refused` to localhost:8080 | Start koki-server or update `koki.sdk.base-url`                           |
| Port conflict           | `Address already in use :8082`         | Change `server.port` or stop conflicting process                          |
| Low coverage            | Build fails at verify                  | Add tests or (not recommended) temporarily reduce thresholds in `pom.xml` |
| Static assets missing   | 404 for images/CSS                     | Verify asset path or configure `asset-url` correctly                      |

### Adjust Logging

Modify logging levels in `application.yml` or local profile:

```yaml
logging:
    level:
        com.wutsi: INFO
        org.springframework: INFO
```

### View Running Threads

Useful for diagnosing executor configuration:

```bash
jcmd $(pgrep -f koki-portal-public) Thread.print | head -50
```

[REPLACE OR REMOVE IF NOT APPLICABLE]

## Next Steps

After setup:

1. Connect to production API by changing `koki.sdk.base-url`.
2. Configure S3 for static assets in test/prod profiles.
3. Tune cache TTL and executor thread pool for load.
4. Add i18n messages in `messages_*.properties`.
5. Implement additional Thymeleaf templates under `templates/`.

## Additional Resources

- [README.md](README.md)
- [koki-server](../koki-server/README.md)
- [koki-sdk](../koki-sdk/README.md)
- [koki-platform](../koki-platform/README.md)
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)

