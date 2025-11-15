# koki-server

Core REST API backend for the Koki multi-tenant real estate platform, providing tenant-aware business services for
accounts, listings, offers, leads, messaging, files, and more.

[![koki-server-master](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml)
[![koki-server-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml)
[![Code Coverage](../../.github/badges/koki-server-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml)

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Configuration](#configuration)
    - [Running the Project](#running-the-project)
    - [Running Tests](#running-tests)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [High-Level System Diagram](#high-level-system-diagram)
- [API Reference](#api-reference)
- [License](#license)

## Features

- **Multi-Tenant Architecture**: Tenant isolation with `X-Tenant-ID` header enforcement
- **Authentication & Authorization**: JWT-based authentication with role-based access control (RBAC)
- **Account Management**: CRUD operations for accounts with custom attributes
- **Contact Management**: Store and manage contacts with phone numbers and emails
- **Lead Management**: Track and manage property leads with status workflows
- **Listing Management**: Property listings with categories, amenities, and location data
- **Offer Management**: Create and manage offers with versioning support
- **Message Management**: Internal messaging system for tenant communication
- **File Management**: Upload, download, and manage files with content type detection
- **Agent Management**: Agent profiles with performance metrics
- **Reference Data**: Categories, amenities, locations, and other configurable reference data
- **Email Notifications**: Template-based email system using Thymeleaf
- **Scheduled Tasks**: Automated background jobs for invitations, metrics, and cleanup
- **API Documentation**: Interactive Swagger UI with grouped endpoints
- **Database Migrations**: Flyway-based schema versioning and migrations
- **Caching**: Redis-based caching for improved performance
- **Message Queue**: RabbitMQ integration for asynchronous processing

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple?logo=kotlin) ![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Frameworks

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green?logo=springsecurity) ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.x-green?logo=spring) ![Hibernate](https://img.shields.io/badge/Hibernate-6.x-orange?logo=hibernate)

### Databases

![MySQL](https://img.shields.io/badge/MySQL-9.5-blue?logo=mysql) ![Flyway](https://img.shields.io/badge/Flyway-DB%20Migration-red?logo=flyway)

### Cloud

![AWS S3](https://img.shields.io/badge/AWS%20S3-Storage-orange?logo=amazons3) ![Redis](https://img.shields.io/badge/Redis-Cache-red?logo=redis) ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.2-orange?logo=rabbitmq)

### Tools & Libraries

![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Templates-green?logo=thymeleaf) ![Apache PDFBox](https://img.shields.io/badge/PDFBox-PDF-red?logo=apache) ![Apache POI](https://img.shields.io/badge/POI-Excel-red?logo=apache) ![Apache Tika](https://img.shields.io/badge/Tika-Content%20Detection-red?logo=apache) ![JWT](https://img.shields.io/badge/JWT-Auth0-black?logo=jsonwebtokens) ![SpringDoc](https://img.shields.io/badge/SpringDoc-OpenAPI-green) ![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5)

## Getting Started

### Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+** for dependency management and builds
- **MySQL 8.0+** database server
- **Redis 7.0+** for caching (optional but recommended for production)
- **RabbitMQ 4.0+** for message queue (optional but recommended for production)
- **Git** for version control

### Installation

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-server
```

2. Install dependencies:

```bash
mvn clean install
```

### Configuration

The application uses Spring profiles for environment-specific configuration. Key configuration files are located
in `src/main/resources/`:

- **application.yml**: Default configuration for local development
- **application-test.yml**: Test environment configuration
- **application-prod.yml**: Production environment configuration

#### Database Configuration

Update the database connection in `application.yml`:

```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/koki?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
        username: root
        password: your_password
```

#### Environment Variables

Key environment variables for production:

- `APP_PROFILE`: Spring profile to activate (e.g., `prod`, `test`)
- `PORT`: Server port (default: 8080)
- `JAVA_OPTS`: JVM options for memory and performance tuning
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `REDIS_URL`: Redis connection URL (if using Redis cache)
- `RABBITMQ_URL`: RabbitMQ connection URL (if using message queue)

#### Email Configuration

Configure SMTP settings in `application.yml` for email notifications:

```yaml
spring:
    mail:
        host: smtp.gmail.com
        port: 587
        username: your_email@gmail.com
        password: your_password
        properties:
            mail:
                smtp:
                    auth: true
                    starttls:
                        enable: true
```

### Running the Project

#### Local Development

Run the application using Maven:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/koki-server.jar
```

The server will start on `http://localhost:8080`.

#### Access API Documentation

Once the server is running, access the interactive Swagger UI at:

```
http://localhost:8080/api.html
```

#### Production Deployment

For production deployment (e.g., Heroku), the `Procfile` is configured:

```
web: java $JAVA_OPTS -Dserver.port=$PORT -jar koki-server.jar --spring.profiles.active=$APP_PROFILE
```

### Running Tests

Run all tests:

```bash
mvn test
```

Run tests with coverage report:

```bash
mvn clean test jacoco:report
```

The JaCoCo coverage report will be generated in `target/site/jacoco/index.html`.

#### Integration Tests

Integration tests require MySQL and RabbitMQ services. The GitHub Actions workflow automatically sets up these services.
For local testing:

```bash
# Start MySQL
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=koki mysql:9.5

# Start RabbitMQ
docker run -d -p 5672:5672 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:4.2

# Run tests
mvn test
```

## High-Level Architecture

### Repository Structure

```
koki-server/
├── pom.xml                          # Maven project configuration
├── Procfile                         # Heroku deployment configuration
├── system.properties                # Java version specification
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/wutsi/koki/
│   │   │       ├── Application.kt   # Spring Boot main application
│   │   │       ├── account/         # Account management (entities, services, endpoints)
│   │   │       │   ├── server/
│   │   │       │   │   ├── dao/     # JPA repositories
│   │   │       │   │   ├── domain/  # JPA entities
│   │   │       │   │   ├── endpoint/# REST controllers
│   │   │       │   │   ├── mapper/  # DTO-Entity mappers
│   │   │       │   │   └── service/ # Business logic
│   │   │       ├── agent/           # Agent management
│   │   │       ├── ai/              # AI integration (translation, content generation)
│   │   │       ├── config/          # Spring configuration classes
│   │   │       ├── contact/         # Contact management
│   │   │       ├── email/           # Email service and templates
│   │   │       ├── error/           # Global error handling
│   │   │       ├── file/            # File upload/download management
│   │   │       ├── lead/            # Lead management
│   │   │       ├── listing/         # Property listing management
│   │   │       ├── message/         # Messaging system
│   │   │       ├── module/          # Module and permission management
│   │   │       ├── note/            # Notes functionality
│   │   │       ├── offer/           # Offer management with versioning
│   │   │       ├── refdata/         # Reference data (categories, amenities, locations)
│   │   │       ├── security/        # Authentication and authorization
│   │   │       ├── tenant/          # Multi-tenant management (tenants, users, roles)
│   │   │       └── translation/     # Translation service integration
│   │   └── resources/
│   │       ├── application.yml      # Default configuration
│   │       ├── application-test.yml # Test configuration
│   │       ├── application-prod.yml # Production configuration
│   │       ├── db/
│   │       │   └── migration/       # Flyway database migrations
│   │       │       ├── common/      # 22 SQL migration files for schema versioning
│   │       │       └── local/       # Local development data seeds
│   │       ├── email/               # Email HTML templates (Thymeleaf)
│   │       ├── listing/             # Listing-related resources
│   │       ├── refdata/             # Reference data CSV files
│   │       ├── user/                # User-related resources
│   │       ├── messages.properties  # Internationalization messages
│   │       └── logback.xml          # Logging configuration
│   └── test/
│       └── kotlin/
│           └── com/wutsi/koki/      # Unit and integration tests for all modules
└── target/                          # Build output directory
```

**Key Components:**

- **Application.kt**: Spring Boot entry point with auto-configuration and multi-module entity/repository scanning
- **Domain Modules**: Each business domain (account, listing, lead, etc.) follows a layered architecture:
    - `domain/`: JPA entities representing database tables
    - `dao/`: Spring Data JPA repositories for data access
    - `service/`: Business logic and orchestration
    - `endpoint/`: REST API controllers exposing HTTP endpoints
    - `mapper/`: Bidirectional mapping between DTOs and entities
- **Database Migrations**: Flyway manages schema evolution with versioned SQL scripts
- **Email Templates**: Thymeleaf templates for HTML emails with dynamic content
- **Configuration**: Environment-specific YAML files for database, caching, messaging, and security

### High-Level System Diagram

The koki-server acts as the core REST API backend in the Koki platform:

```
┌──────────────────────────────────────────────────────────────────┐
│                         External Clients                         │
│  - koki-portal (Admin UI)  - koki-portal-public (Public UI)     │
│  - koki-sdk (Client Library) - Chatbots - Mobile Apps           │
└────────────────────────────┬─────────────────────────────────────┘
                             │ HTTP/REST (JSON)
                             │ X-Tenant-ID Header
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                         koki-server                              │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │         Spring Security + JWT Authentication             │   │
│  │  - Token Validation  - Tenant Context  - RBAC            │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │              REST API Endpoints (Controllers)            │   │
│  │  /v1/auth    /v1/accounts   /v1/listings   /v1/leads     │   │
│  │  /v1/offers  /v1/messages   /v1/files      /v1/agents    │   │
│  │  /v1/contacts /v1/notes     /v1/tenants    /v1/users     │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │              Business Services Layer                     │   │
│  │  - AccountService     - ListingService   - LeadService   │   │
│  │  - OfferService       - MessageService   - FileService   │   │
│  │  - AuthenticationService  - EmailService - AiService     │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │          Data Access Layer (Spring Data JPA)             │   │
│  │  - AccountRepository  - ListingRepository - DAO Pattern  │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         koki-platform (Infrastructure Library)           │   │
│  │  - Storage Provider (S3/Local)  - Cache (Redis)          │   │
│  │  - Message Queue (RabbitMQ)     - Logging & Monitoring   │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────────┬─────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐    ┌───────────────┐    ┌──────────────┐
│  MySQL 9.5    │    │  Redis 7.0    │    │ RabbitMQ 4.2 │
│  (Primary DB) │    │  (Cache)      │    │ (Message Q)  │
└───────────────┘    └───────────────┘    └──────────────┘
        │
        ▼
┌───────────────┐
│  Flyway       │
│  (Migrations) │
└───────────────┘
```

**Data Flow:**

1. **Request Entry**: Clients send HTTP requests with `X-Tenant-ID` header and JWT token
2. **Authentication**: Spring Security validates JWT and extracts user context
3. **Tenant Isolation**: TenantContext ThreadLocal ensures data isolation per tenant
4. **Business Logic**: Services orchestrate operations, enforce business rules, and coordinate with external systems
5. **Data Persistence**: JPA repositories interact with MySQL, Flyway ensures schema consistency
6. **Caching**: Redis caches frequently accessed data (reference data, user sessions)
7. **Async Processing**: RabbitMQ handles background tasks (email sending, notifications, metrics)
8. **Storage**: Files uploaded via File API are stored in S3 or local filesystem via koki-platform abstraction

## API Reference

The koki-server exposes RESTful APIs organized by business domain. All endpoints require:

- **X-Tenant-ID header**: Specifies the tenant context for multi-tenancy
- **Authorization header**: JWT Bearer token for authenticated endpoints

### API Groups

| Group              | Description                                       | Base Path                                                         |
|--------------------|---------------------------------------------------|-------------------------------------------------------------------|
| **Authentication** | Login and token generation                        | `/v1/auth`                                                        |
| **Accounts**       | Account and attribute management                  | `/v1/accounts`, `/v1/attributes`                                  |
| **Agents**         | Agent profiles and metrics                        | `/v1/agents`                                                      |
| **Contacts**       | Contact information management                    | `/v1/contacts`                                                    |
| **Files**          | File upload, download, and metadata               | `/v1/files`                                                       |
| **Leads**          | Lead tracking and management                      | `/v1/leads`                                                       |
| **Listings**       | Property listing CRUD operations                  | `/v1/listings`                                                    |
| **Messages**       | Internal messaging system                         | `/v1/messages`                                                    |
| **Modules**        | Module and permission management                  | `/v1/modules`, `/v1/permissions`                                  |
| **Notes**          | Note management                                   | `/v1/notes`                                                       |
| **Offers**         | Offer and offer version management                | `/v1/offers`, `/v1/offer-versions`                                |
| **RefData**        | Reference data (categories, amenities, locations) | `/v1/amenities`, `/v1/categories`, `/v1/locations`, `/v1/refdata` |
| **Tenant**         | Tenant, user, role, and invitation management     | `/v1/tenants`, `/v1/users`, `/v1/roles`, `/v1/invitations`        |

### Example Endpoints

#### Authentication

```
POST   /v1/auth/login          # Authenticate user and receive JWT token
```

#### Accounts

```
POST   /v1/accounts            # Create a new account
GET    /v1/accounts            # Search accounts with filters
GET    /v1/accounts/{id}       # Get account details by ID
POST   /v1/accounts/{id}       # Update account information
DELETE /v1/accounts/{id}       # Delete an account
```

#### Listings

```
POST   /v1/listings            # Create a new property listing
GET    /v1/listings            # Search listings with filters
GET    /v1/listings/{id}       # Get listing details by ID
POST   /v1/listings/{id}       # Update listing information
DELETE /v1/listings/{id}       # Delete a listing
```

#### Files

```
POST   /v1/files               # Upload a file
GET    /v1/files               # Search files
GET    /v1/files/{id}          # Get file metadata
GET    /v1/files/{id}/content  # Download file content
DELETE /v1/files/{id}          # Delete a file
```

#### Tenants & Users

```
POST   /v1/tenants             # Create a new tenant
GET    /v1/tenants/{id}        # Get tenant details
POST   /v1/users               # Create a new user
GET    /v1/users               # Search users
POST   /v1/invitations         # Invite a user to tenant
```

### Interactive API Documentation

For complete API documentation with request/response schemas and interactive testing, access the Swagger UI:

```
http://localhost:8080/api.html
```

The API documentation is automatically generated using SpringDoc OpenAPI and includes:

- Request/response body schemas
- Query parameters and headers
- Example payloads
- Error responses
- Try-it-out functionality for testing endpoints

## License

This project is licensed under the MIT License. See the [LICENSE](../../LICENSE.md) file for details.

