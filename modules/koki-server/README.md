# koki-server

A Spring Boot Kotlin backend providing multi-tenant REST APIs, security, payments, messaging, and document processing
for the Koki platform.

[![koki-server CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml)

[![koki-server CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml)

![Coverage](../../.github/badges/koki-server-jococo.svg)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

![MySQL](https://img.shields.io/badge/Database-MySQL-blue.svg)

## About the Project

`koki-server` is the core backend service of the Koki platform. It exposes tenant-aware REST APIs for accounts,
contacts, listings, offers, leads, messaging, files, payments, and configuration. The service implements JWT-based
security, structured error handling, asynchronous jobs, scheduled tasks, and rich document/email generation. It
integrates with external providers (Stripe, PayPal), supports content extraction (Tika, PDFBox, POI), and produces
OpenAPI documentation for all endpoints.

### Features

- **Secure Multi-Tenant APIs** – JWT auth, per-request `X-Tenant-ID` header enforcement, role-aware endpoints.
- **Robust Persistence Layer** – Spring Data JPA + Flyway migrations, MySQL storage, HikariCP pooling.
- **Payments & Transactions** – Stripe & PayPal integration for initiating and reconciling payment workflows.
- **Document & File Processing** – PDF generation, Excel/Word parsing, metadata and language detection via Tika.
- **Messaging & Email Templating** – Thymeleaf templates, HTML rendering, scheduled and asynchronous delivery.

## Getting Started

Follow these steps to run the service locally.

### Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **MySQL 8+** (password-less local root user recommended)
- **Git**

### 1. Clone the Repository

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-server
```

### 2. Create Local Database (Password-less Root)

```bash
mysql -u root <<'SQL'
CREATE DATABASE IF NOT EXISTS koki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SQL
```

### 3. Configure Application (Optional Override)

Default datasource lives in `src/main/resources/application.yml`. To override without editing the file, export
environment variables or create an `application-local.yml` and run with `--spring.profiles.active=local`.

Example minimal `application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/koki?serverTimezone=UTC
    username: root
    password: ""
```

### 4. Build

```bash
mvn clean install
```

Generates the JAR and runs unit tests & coverage (badge updated via CI).

### 5. Run

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/koki-server-VERSION_NUMBER.jar
```

Service starts on: `http://localhost:8080`

### 6. API Documentation (Swagger / OpenAPI)

Visit:

```
http://localhost:8080/api.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

### 7. Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected:

```json
{
    "status": "UP"
}
```

### 8. Authentication Flow

Obtain JWT token (example endpoint – adapt if login path changes):

```bash
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo"}'
```

Use token + tenant header in subsequent calls:

```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "X-Tenant-ID: 1" \
     http://localhost:8080/v1/accounts/1
```

### 9. Running Migrations

Flyway runs automatically at startup. To verify applied migrations:

```bash
mysql -u root -e "USE koki; SHOW TABLES;"
```

### 10. Common Environment Overrides

| Purpose            | Property                    | Example      |
|--------------------|-----------------------------|--------------|
| Increase DB pool   | `database.pool-size`        | `8`          |
| Enable SQL logging | `spring.jpa.show-sql`       | `true`       |
| Change port        | `server.port`               | `9090`       |
| Swagger path       | `springdoc.swagger-ui.path` | `/docs.html` |

## License

See the root [License](../../LICENSE.md).
