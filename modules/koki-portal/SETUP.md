# Setup Guide - koki-portal

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)

## Prerequisites

Before running the koki-portal application, ensure you have the following software installed:

- **Java Development Kit (JDK) 17** or higher
    - Verify: `java -version`
    - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

- **Maven 3.8+** for dependency management and build
    - Verify: `mvn -version`
    - Download: [Apache Maven](https://maven.apache.org/download.cgi)

- **koki-server** running locally or remotely (required for backend REST APIs)
    - Default base URL: `http://localhost:8080`

- **Redis 7.x+** (optional for caching and session management in production)
    - Verify: `redis-cli ping` (should return `PONG`)

- **RabbitMQ 4.x+** (optional for event publishing integration)
    - Verify: `rabbitmqctl status`

### Optional Tools

- **Docker** (to run Redis and RabbitMQ quickly)
- **AWS CLI** (if using S3 for file storage)
- **Chromium Browser** (for Selenium integration tests)
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
cd modules/koki-portal
mvn clean package
```

Artifact created: `target/koki-portal-1.0.0-SNAPSHOT.jar`

## Database Setup

This module does **not** directly connect to a database. All dynamic data is obtained via REST APIs from `koki-server`.

If you need to manage backend data:

- Ensure `koki-server` has its database configured and running
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
    port: 8081

koki:
    webapp:
        client-id: koki-portal
        base-url: http://localhost:8081
        asset-url: ""
    rest:
        connection-timeout: 15000
        read-timeout: 15000
    sdk:
        base-url: http://localhost:8080   # koki-server
    toggles:
        paypal: true
        mobile-money: true
        modules:
            agent: true
            account: false
            contact: true
            image: true
            file: false
            listing: true
            message: true
            offer: false

wutsi:
    platform:
        ai:
            enabled: false
        cache:
            name: koki
            type: none                        # or redis
            ttl: 86400
            redis:
                url: redis://:test@localhost:6379
        executor:
            thread-pool:
                name: koki-portal
                size: 16
        mq:
            type: none                        # or rabbitmq
        storage:
            enabled: true
            type: local                       # or s3
            local:
                directory: ${user.home}/__wutsi
                base-url: http://localhost:8081
                servlet-path: /local-storage
            s3:
                bucket: koki-test
                region: us-east-1
                access-key: ""
                secret-key: ""
```

### Feature Toggles

The portal includes feature toggles to enable/disable specific modules:

```yaml
koki:
    toggles:
        paypal: true                      # Enable PayPal integration
        mobile-money: true                # Enable mobile money payments
        modules:
            agent: true                     # Agent management module
            account: false                  # Account management module
            contact: true                   # Contact management module
            image: true                     # Image/gallery management
            file: false                     # File management module
            listing: true                   # Property listings module
            message: true                   # Messaging module
            offer: false                    # Offer management module
```

### Test Profile (application-test.yml)

Used in CI and automated tests:

```yaml
koki:
    webapp:
        asset-url: https://com-wutsi-koki-test.s3.amazonaws.com/static/koki-portal
        base-url: ${APP_URL}
    sdk:
        base-url: ${KOKI_SERVER_URL}

wutsi:
    platform:
        cache:
            type: redis
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
            type: rabbitmq
            rabbitmq:
                url: ${CLOUDAMQP_URL}
```

### Production Profile (application-prod.yml)

```yaml
koki:
    webapp:
        asset-url: https://com-wutsi-koki-prod.s3.amazonaws.com/static/koki-portal
        base-url: ${APP_URL}
    sdk:
        base-url: ${KOKI_SERVER_URL}

wutsi:
    platform:
        cache:
            type: redis
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
            type: rabbitmq
            rabbitmq:
                url: ${CLOUDAMQP_URL}
```

### Environment Variables

Common environment variables for test/prod deployments:

```bash
export APP_URL="https://admin.example.com"
export KOKI_SERVER_URL="https://api.example.com"
export REDISCLOUD_URL="redis://:password@redis-host:6379"
export CLOUDAMQP_URL="amqp://user:pass@rabbitmq-host/vhost"
export AWS_ACCESS_KEY="YOUR_ACCESS_KEY"
export AWS_SECRET_KEY="YOUR_SECRET_KEY"
```

Local development overrides:

```bash
export SERVER_PORT=8081
export KOKI_SERVER_URL=http://localhost:8080
export REDIS_URL=redis://:test@localhost:6379
```

### Security Configuration

JWT-based authentication is configured via Spring Security. Session management:

```yaml
server:
    servlet:
        session:
            timeout: 30m              # Session timeout
            cookie:
                http-only: true
                secure: true            # Enable in production with HTTPS
```

### File Upload Configuration

Adjust file upload limits:

```yaml
spring:
    servlet:
        multipart:
            max-file-size: 50MB
            max-request-size: 50MB
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
java -jar -Dspring.profiles.active=local target/koki-portal-1.0.0-SNAPSHOT.jar
```

### Verify Application Startup

Check health endpoint:

```bash
curl http://localhost:8081/actuator/health
```

Expected response:

```json
{
    "status": "UP"
}
```

View info (includes git commit metadata):

```bash
curl http://localhost:8081/actuator/info
```

### Access the Portal

Open in browser:

```
http://localhost:8081/
```

You'll need to:

1. **Login** with valid credentials (managed via koki-server)
2. Navigate through the admin dashboard
3. Manage accounts, listings, contacts, leads, offers, messages, etc.

### Using Local Storage for Uploaded Files

Uploaded content is stored in:

```
${HOME}/__wutsi
```

Verify directory exists:

```bash
ls -la ${HOME}/__wutsi
```

### Enable Redis for Session Management (Optional)

If Redis is not running but referenced in configuration, either:

- Start Redis (recommended for production-like testing)
- Or change `wutsi.platform.cache.type` to `none` for local development

To use Redis:

```bash
# Start Redis
docker run -d --name koki-redis -p 6379:6379 redis:7-alpine redis-server --requirepass test

# Test connection
redis-cli -a test ping
```

### Connecting to koki-server

Ensure `koki.sdk.base-url` points to a running instance of koki-server:

```yaml
koki:
    sdk:
        base-url: http://localhost:8080
```

Verify koki-server is accessible:

```bash
curl http://localhost:8080/actuator/health
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
4. Enforce thresholds (line >= 80%, class >= 80%)

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

### Selenium Integration Tests

The portal includes Selenium-based UI tests. Requirements:

- Chromium browser installed
- ChromeDriver in PATH

Install ChromeDriver:

```bash
# macOS
brew install chromedriver

# Linux (Ubuntu/Debian)
sudo apt-get install chromium-chromedriver

# Verify
chromedriver --version
```

Run Selenium tests:

```bash
mvn verify
```

### Troubleshooting Test Failures

- Ensure `koki-server` dependency endpoints are mocked or running
- Verify environment variables for test profile (e.g., `APP_URL`, `KOKI_SERVER_URL`)
- For Selenium tests, ensure browser driver is compatible with browser version
- Check test logs: `target/surefire-reports/`

## Troubleshooting

| Issue                   | Symptom                                | Resolution                                                 |
|-------------------------|----------------------------------------|------------------------------------------------------------|
| Redis connection failed | `Connection refused`                   | Start Redis container or set cache type to `none`          |
| RabbitMQ not reachable  | Timeouts                               | Confirm RabbitMQ running or set mq type to `none`          |
| SDK calls failing       | `Connection refused` to localhost:8080 | Start koki-server or update `koki.sdk.base-url`            |
| Port conflict           | `Address already in use :8081`         | Change `server.port` or stop conflicting process           |
| Low coverage            | Build fails at verify                  | Add tests or temporarily reduce thresholds in `pom.xml`    |
| Static assets missing   | 404 for images/CSS                     | Verify asset path or configure `asset-url` correctly       |
| Login fails             | Authentication errors                  | Verify koki-server is running and JWT secret is configured |
| File upload fails       | 413 Payload Too Large                  | Adjust `max-file-size` and `max-request-size` limits       |
| Session timeout         | Frequent logouts                       | Increase `server.servlet.session.timeout`                  |

### Adjust Logging

Modify logging levels in `application.yml` or local profile:

```yaml
logging:
    level:
        com.wutsi: DEBUG
        org.springframework: INFO
        org.springframework.security: DEBUG
```

### View Running Sessions

Useful for diagnosing session issues:

```bash
# Check active sessions in Redis
redis-cli -a test keys "spring:session:*"
```

### Check Application Logs

View recent logs:

```bash
tail -f logs/application.log
```

Or check console output when running via Maven or JAR.

### Clear Local Storage

If experiencing file storage issues:

```bash
rm -rf ${HOME}/__wutsi
mkdir -p ${HOME}/__wutsi
```

## Next Steps

After setup:

1. **Login**: Use credentials from koki-server to access the portal
2. **Explore Modules**: Navigate through account, listing, contact, lead, and message management
3. **Configure Settings**: Set up email (SMTP), AI integration, and system preferences
4. **Create Content**: Add listings, accounts, contacts, and manage offers
5. **User Management**: Create users, assign roles, and manage permissions
6. **Feature Toggles**: Enable/disable modules based on your tenant needs
7. **Customize UI**: Adjust templates in `src/main/resources/templates/`
8. **Production Deployment**: Configure S3, Redis, RabbitMQ for production environment

## Additional Resources

- [README.md](README.md) - Project overview and architecture
- [CONTRIBUTING.md](../../CONTRIBUTING.md) - Contribution guidelines
- [koki-server](../koki-server/README.md) - Backend REST API server
- [koki-sdk](../koki-sdk/README.md) - Client SDK for API integration
- [koki-platform](../koki-platform/README.md) - Shared platform utilities
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)

