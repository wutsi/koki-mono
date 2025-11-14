# koki-server

A Spring Boot Kotlin backend providing multi-tenant REST APIs, security, payments, messaging, and document processing
for the Koki platform.

![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml/badge.svg)

![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml/badge.svg)

![JaCoCo](../../.github/badges/koki-server-jacoco.svg)

![Java](https://img.shields.io/badge/Java-17-blue)

![Kotlin](https://img.shields.io/badge/Kotlin-language-purple)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)

![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)

![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0-orange)

![Redis](https://img.shields.io/badge/Redis-7.0-red)

## Table of Contents

- [About the Project](#about-the-project)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Project](#running-the-project)
    - [Running Tests](#running-tests)
- [API References](#api-references)
- [License](#license)

## About the Project

The **koki-server** is the core backend service of the Koki platform. It exposes tenant-aware REST APIs for managing
accounts, contacts, listings, offers, leads, messages, files, payments, and configuration. The service implements
JWT-based authentication and authorization, structured error handling, asynchronous job processing, scheduled tasks, and
rich document/email generation capabilities.

Key features include:

- **Secure Multi-Tenant APIs**: JWT authentication with per-request tenant ID enforcement and role-based access control
- **Comprehensive Business Logic**: Domain-driven design with services for accounts, contacts, listings, leads,
  messages, files, payments, and more
- **Robust Persistence**: Spring Data JPA with Flyway migrations, MySQL storage, and HikariCP connection pooling
- **Payment Integration**: Stripe and PayPal integration for payment processing and reconciliation workflows
- **Document Processing**: PDF generation, Excel/Word parsing, metadata extraction, and language detection using Apache
  Tika
- **Messaging & Templates**: Email templating with Thymeleaf, HTML rendering, and asynchronous delivery
- **OpenAPI Documentation**: Auto-generated API documentation accessible via Swagger UI

## Getting Started

### Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+**
- **MySQL 8.0+** (with passwordless root user for local development)
- **RabbitMQ 4.0+**
- **Redis 7.0+**

### Installation

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-server
```

2. Create the local database:

```bash
mysql -u root <<'SQL'
CREATE DATABASE IF NOT EXISTS koki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SQL
```

3. Build the project:

```bash
mvn clean install
```

4. Configure the application by creating **application-local.yml** (optional):

```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/koki?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
        username: root
        password: ""
wutsi:
    platform:
        mq:
            rabbitmq:
                url: amqp://localhost
        cache:
            redis:
                url: redis://localhost:6379
```

### Running the Project

Run the application locally:

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/koki-server-VERSION_NUMBER.jar
```

The server will start on port **8080** by default.

Verify the service is running:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{
    "status": "UP"
}
```

### Running Tests

Execute unit tests:

```bash
mvn test
```

Run all tests including integration tests:

```bash
mvn verify
```

Generate test coverage report:

```bash
mvn clean test jacoco:report
```

The coverage report will be available at **target/site/jacoco/index.html**.

## API References

The koki-server exposes RESTful APIs organized by domain. Access the interactive API documentation via Swagger UI:

```
http://localhost:8080/api.html
```

OpenAPI JSON specification:

```
http://localhost:8080/v3/api-docs
```

### Authentication

Obtain a JWT token by authenticating:

```bash
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo"}'
```

Use the token with the tenant header in subsequent requests:

```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "X-Tenant-ID: 1" \
     http://localhost:8080/v1/accounts/1
```

### Key API Domains

- **/v1/accounts**: User account management
- **/v1/contacts**: Contact management
- **/v1/listings**: Product/service listings
- **/v1/leads**: Lead tracking and management
- **/v1/messages**: Messaging and notifications
- **/v1/files**: File upload and management
- **/v1/payments**: Payment processing and transactions
- **/v1/config**: Tenant configuration

For detailed endpoint descriptions, request/response schemas, and examples, refer to the Swagger UI documentation.

## License

This project is licensed under the MIT License. See [LICENSE.md](../../LICENSE.md) for details.

