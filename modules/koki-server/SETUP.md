# Setup Guide - koki-server

Comprehensive setup instructions for local development and validation of the `koki-server` module.

## Prerequisites

Install the following components before running the server:

- Java JDK 17+
  Check: `java -version`
- Maven 3.8+
  Check: `mvn -version`
- MySQL 8.x (README mentions 9.5 – treat as typo)
  Check: `mysql --version`
- RabbitMQ 4.x+ (for domain event publishing)
  Check: `rabbitmqctl status`
- Redis 7.x+ (optional, caching)
  Check: `redis-cli ping`
- (Optional) Docker + Docker Compose for containerized services
- (Optional) AWS CLI if using S3 storage backend

### Quick Start With Docker (Optional)

```bash
# MySQL
docker run -d --name koki-mysql -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=koki -p 3306:3306 mysql:8

# RabbitMQ (with management UI)
docker run -d --name koki-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Redis
docker run -d --name koki-redis -p 6379:6379 redis:7-alpine
```

RabbitMQ UI: http://localhost:15672 (default: guest / guest)

## Installation

Clone the mono-repo and build shared modules first:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono
mvn clean install -DskipTests
```

Build the server module:

```bash
cd modules/koki-server
mvn clean package
```

Artifact: `target/koki-server-1.0.0-SNAPSHOT.jar` (version may vary).

## Database Setup

The server uses MySQL with Flyway migrations located in:

```
src/main/resources/db/migration/common
src/main/resources/db/migration/local   # Dev/seed data
src/main/resources/db/migration/test    # Test profile data
```

Flyway runs automatically at startup. Ensure the database exists and your user has privileges.

### Create Database

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS koki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### (Re)Initialize During Development

If you need a clean slate:

```bash
mysql -u root -p -e "DROP DATABASE koki; CREATE DATABASE koki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Then restart the application to re-run migrations.

### Seed Data

Local profile includes seed scripts (e.g. `V1_31_1__setup-tenant.sql`) that insert a sample tenant, roles, user, and
configuration. Activate the `local` profile to load them (see Configuration section).

## Configuration

Base configuration: `src/main/resources/application.yml` (datasource, Flyway locations, JPA, logging, Swagger grouping,
etc.).
Use profile-specific files (e.g. `application-test.yml`, `application-prod.yml`) or environment variables to override
settings.

### Core Settings (Default application.yml)

- Server port: `8080`
- Datasource URL: `jdbc:mysql://localhost:3306/koki?...`
- Flyway locations: `common, local`
- Swagger UI path: `/api.html`
- Logging: `com.wutsi` at DEBUG (adjust for production)

### Example Local Override (`application-local.yml`)

Create `src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/koki?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: secret
  flyway:
    locations: "classpath:db/migration/common,classpath:db/migration/local"

logging:
  level:
    com.wutsi: DEBUG

koki:
  webapp:
    server-url: http://localhost:8080
```

Run with: `-Dspring-boot.run.profiles=local`.

### Environment Variables

You can override configuration without profile files:

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/koki?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="secret"
export SERVER_PORT=8080
export RABBITMQ_URL="amqp://localhost"
export REDIS_URL="redis://localhost:6379"
export STORAGE_TYPE=local          # or s3
export AWS_ACCESS_KEY_ID="REPLACE"
export AWS_SECRET_ACCESS_KEY="REPLACE"
export AWS_S3_BUCKET="REPLACE"
export SPRING_MAIL_HOST="smtp.example.com"
export SPRING_MAIL_PORT=587
export SPRING_MAIL_USERNAME="REPLACE"
export SPRING_MAIL_PASSWORD="REPLACE"
```

For tests (see `application-test.yml`), variables like `SPRING_DATABASE_URL`, `CLOUDAMQP_URL`, `REDISCLOUD_URL` may be
required in CI.

### Storage Backend

- Local: Files stored under a directory (configured via platform storage settings).
- S3: Requires bucket, region, access/secret keys.
  Switch using `STORAGE_TYPE` (local | s3).

### Email / SMTP

If using outbound email features, configure SMTP host, port, credentials. GreenMail is used in tests (via dependency)
for simulation.

### Security & Auth

JWT support via `java-jwt` library. Ensure secret keys or signing configuration (if external) are provided separately (
not shown in sample YAML).

## Running the Project

### Run via Maven (Dev)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

(Omits packaging step; recompiles on changes.)

### Run Packaged JAR

```bash
mvn clean package
java -jar -Dspring.profiles.active=local target/koki-server-1.0.0-SNAPSHOT.jar
```

### Verify Health & Info

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/flyway
```

### API Documentation

Swagger UI (SpringDoc) is available at:

```bash
open http://localhost:8080/api.html    # macOS
# or
xdg-open http://localhost:8080/api.html
```

Grouped endpoints are defined (authentication, accounts, listing, etc.).

### RabbitMQ & Redis Checks

```bash
rabbitmqctl list_queues
redis-cli ping
```

Ensure these services are running before testing event or cache features.

## Running Tests

### Unit & Integration

```bash
mvn test
```

Runs all tests in the module (`spring-boot-starter-test`, TestNG support, Spring Security test utilities).

### Full Verification + Coverage

```bash
mvn clean verify
```

Enforces JaCoCo thresholds (line >= 92%, class >= 92%). Fails build if below.

### Coverage Report

```bash
open target/site/jacoco/index.html      # macOS
xdg-open target/site/jacoco/index.html  # Linux
```

### Focused Tests

```bash
mvn -Dtest=AccountServiceTest test
mvn -Dtest=*Account* test
```

### Skip Tests (Rapid Build)

```bash
mvn clean package -DskipTests
```

## Troubleshooting

| Issue                | Symptom                        | Resolution                                                                 |
|----------------------|--------------------------------|----------------------------------------------------------------------------|
| DB auth failure      | `Access denied for user`       | Verify credentials/env vars; ensure MySQL running and user has rights      |
| Flyway errors        | Migration failure stacktrace   | Check ordering & naming (`V<version>__descr.sql`); rollback DB and restart |
| Port conflict        | `Address already in use :8080` | Change `server.port` in profile or stop conflicting process                |
| RabbitMQ unreachable | Connection refused             | Start container/service; validate `RABBITMQ_URL`                           |
| Redis cache errors   | Timeout or refused             | Optional: disable cache or start Redis locally                             |
| Low coverage         | Build fails at verify          | Improve tests or temporarily adjust thresholds (not recommended)           |

### Log Configuration

Adjust log levels in profile YAML:

```yaml
logging:
  level:
    com.wutsi: INFO
    org.springframework: INFO
```

## Next Steps

- Explore domain endpoints via Swagger UI.
- Add/modify seed migrations under `db/migration/local` for custom dev data.
- Integrate with other modules (e.g., tracking server) via RabbitMQ events.

## Reference

- `application.yml` for defaults
- `application-test.yml` for CI/test overrides
- `pom.xml` for dependencies & coverage thresholds
- `db/migration/*` for schema + seed data
