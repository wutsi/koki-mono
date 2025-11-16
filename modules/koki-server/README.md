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

- **Multi-Tenant Architecture**: Tenant isolation with `X-Tenant-ID` header enforcement ensuring complete data
  separation.
- **Authentication & Authorization**: JWT-based authentication with role-based access control (RBAC) for secure API
  access.
- **Account Management**: Complete CRUD operations for accounts with custom attributes and flexible data model.
- **Contact Management**: Store and manage contacts with phone numbers, emails, and relationship tracking.
- **Lead Management**: Track and manage property leads with status workflows and assignment capabilities.
- **Listing Management**: Property listings with categories, amenities, location data, and rich metadata.
- **Offer Management**: Create and manage offers with versioning support for tracking offer history.
- **Message Management**: Internal messaging system for tenant communication and collaboration.
- **File Management**: Upload, download, and manage files with automatic content type detection and storage.
- **Agent Management**: Agent profiles with performance metrics and activity tracking.
- **Reference Data**: Categories, amenities, locations, and other configurable reference data management.
- **Email Notifications**: Template-based email system using Thymeleaf for dynamic content generation.
- **Scheduled Tasks**: Automated background jobs for invitations, metrics calculation, and data cleanup.
- **API Documentation**: Interactive Swagger UI with grouped endpoints for easy API exploration.
- **Database Migrations**: Flyway-based schema versioning and migrations for consistent database evolution.
- **Caching**: Redis-based caching for improved performance and reduced database load.
- **Message Queue**: RabbitMQ integration for asynchronous processing and event-driven architecture.
- **AI Integration**: Translation and content generation capabilities powered by AI services.

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple?logo=kotlin)
![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Frameworks

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green?logo=springsecurity)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.x-green?logo=spring)
![Hibernate](https://img.shields.io/badge/Hibernate-6.x-orange?logo=hibernate)

### Databases

![MySQL](https://img.shields.io/badge/MySQL-9.0-blue?logo=mysql)
![Flyway](https://img.shields.io/badge/Flyway-DB%20Migration-red?logo=flyway)

### Cloud

![AWS S3](https://img.shields.io/badge/AWS%20S3-Storage-orange?logo=amazons3)
![Redis](https://img.shields.io/badge/Redis-7.0+-red?logo=redis)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.0+-orange?logo=rabbitmq)

### Tools & Libraries

![Maven](https://img.shields.io/badge/Maven-3.8+-blue?logo=apachemaven)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green?logo=thymeleaf)
![Apache PDFBox](https://img.shields.io/badge/Apache%20PDFBox-PDF-red?logo=apache)
![Apache POI](https://img.shields.io/badge/Apache%20POI-Excel-red?logo=apache)
![Apache Tika](https://img.shields.io/badge/Apache%20Tika-Content-red?logo=apache)
![JWT](https://img.shields.io/badge/JWT-Auth0-black?logo=jsonwebtokens)
![SpringDoc](https://img.shields.io/badge/SpringDoc-OpenAPI-green)
![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5)

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

Update the database connection in **application.yml**:

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

Configure SMTP settings in **application.yml** for email notifications:

```yaml
spring:
    mail:
        host: smtp.gmail.com
        port: 587
        username: your_email@gmail.com
        password: your_app_password
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

For production deployment (e.g., Heroku), the **Procfile** is configured:

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
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=koki mysql:9.0

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
│   │   │       ├── account/         # Account management module
│   │   │       │   └── server/
│   │   │       │       ├── dao/     # JPA repositories
│   │   │       │       ├── domain/  # JPA entities
│   │   │       │       ├── endpoint/# REST controllers
│   │   │       │       ├── mapper/  # DTO-Entity mappers
│   │   │       │       └── service/ # Business logic
│   │   │       ├── agent/           # Agent management module
│   │   │       │   └── server/      # Agent profiles and metrics
│   │   │       ├── ai/              # AI integration module
│   │   │       │   └── server/      # Translation and content generation
│   │   │       ├── config/          # Spring configuration classes
│   │   │       │   ├── CacheConfiguration.kt
│   │   │       │   ├── JacksonConfiguration.kt
│   │   │       │   ├── OpenAPIConfiguration.kt
│   │   │       │   └── SecurityConfiguration.kt
│   │   │       ├── contact/         # Contact management module
│   │   │       │   └── server/      # Phone numbers and emails
│   │   │       ├── email/           # Email service module
│   │   │       │   ├── server/
│   │   │       │   │   ├── generator/  # Email content generators
│   │   │       │   │   └── service/    # Email sending service
│   │   │       │   └── template/       # Email templates
│   │   │       ├── error/           # Global error handling
│   │   │       │   └── server/
│   │   │       │       └── endpoint/   # Error handler controller
│   │   │       ├── file/            # File management module
│   │   │       │   └── server/      # Upload/download operations
│   │   │       ├── lead/            # Lead management module
│   │   │       │   └── server/      # Lead tracking and workflows
│   │   │       ├── listing/         # Property listing module
│   │   │       │   └── server/      # Property CRUD operations
│   │   │       ├── message/         # Messaging system module
│   │   │       │   └── server/      # Internal communication
│   │   │       ├── module/          # Module and permission management
│   │   │       │   └── server/      # Access control modules
│   │   │       ├── note/            # Notes functionality module
│   │   │       │   └── server/      # Note CRUD operations
│   │   │       ├── offer/           # Offer management module
│   │   │       │   └── server/      # Offer versioning
│   │   │       ├── refdata/         # Reference data module
│   │   │       │   └── server/      # Categories, amenities, locations
│   │   │       ├── security/        # Security module
│   │   │       │   └── server/      # Authentication and JWT handling
│   │   │       ├── tenant/          # Multi-tenant management module
│   │   │       │   └── server/      # Tenants, users, roles
│   │   │       └── translation/     # Translation service module
│   │   │           └── server/      # Language translation
│   │   └── resources/
│   │       ├── application.yml      # Default configuration
│   │       ├── application-test.yml # Test configuration
│   │       ├── application-prod.yml # Production configuration
│   │       ├── db/
│   │       │   └── migration/       # Flyway database migrations
│   │       │       ├── common/      # Schema versioning SQL files
│   │       │       │   ├── V1_0__initial.sql
│   │       │       │   ├── V1_1__refdata.sql
│   │       │       │   ├── V1_2__module.sql
│   │       │       │   ├── V1_3__tenant.sql
│   │       │       │   ├── V1_4__account.sql
│   │       │       │   ├── V1_5__contact.sql
│   │       │       │   ├── V1_6__file.sql
│   │       │       │   ├── V1_7__note.sql
│   │       │       │   ├── V1_8__email.sql
│   │       │       │   ├── V1_14__ai.sql
│   │       │       │   ├── V1_16__translation.sql
│   │       │       │   ├── V1_19__message.sql
│   │       │       │   ├── V1_30__listing.sql
│   │       │       │   ├── V1_31__offer.sql
│   │       │       │   ├── V1_32__agent.sql
│   │       │       │   └── V1_33__lead.sql
│   │       │       └── local/       # Local development data seeds
│   │       ├── email/               # Email HTML templates (Thymeleaf)
│   │       ├── refdata/             # Reference data CSV files
│   │       ├── messages.properties  # Internationalization messages
│   │       └── logback.xml          # Logging configuration
│   └── test/
│       └── kotlin/
│           └── com/wutsi/koki/      # Unit and integration tests
│               ├── account/         # Account module tests
│               ├── agent/           # Agent module tests
│               ├── contact/         # Contact module tests
│               ├── email/           # Email service tests
│               ├── file/            # File management tests
│               ├── lead/            # Lead module tests
│               ├── listing/         # Listing module tests
│               ├── message/         # Message module tests
│               ├── note/            # Note module tests
│               ├── offer/           # Offer module tests
│               ├── refdata/         # Reference data tests
│               ├── security/        # Security tests
│               └── tenant/          # Tenant module tests
└── target/                          # Build output directory
    ├── jacoco.exec                  # Code coverage data
    ├── koki-server.jar              # Packaged application
    ├── classes/                     # Compiled classes
    ├── test-classes/                # Compiled test classes
    └── surefire-reports/            # Test reports
```

**Key Components:**

- **Application.kt**: Spring Boot entry point with auto-configuration, multi-module entity scanning, and JPA repository
  configuration.
- **Domain Modules**: Each business domain (account, listing, lead, etc.) follows a layered architecture:
    - `domain/`: JPA entities representing database tables with relationships and constraints.
    - `dao/`: Spring Data JPA repositories for data access with custom query methods.
    - `service/`: Business logic, validation, orchestration, and transaction management.
    - `endpoint/`: REST API controllers exposing HTTP endpoints with validation.
    - `mapper/`: Bidirectional mapping between DTOs and entities using manual mapping.
- **Database Migrations**: Flyway manages schema evolution with versioned SQL scripts in chronological order.
- **Email Templates**: Thymeleaf templates for HTML emails with dynamic content and styling.
- **Configuration**: Environment-specific YAML files for database, caching, messaging, security, and API documentation.
- **Security**: JWT-based authentication with Spring Security, role-based access control, and tenant context management.

### High-Level System Diagram

The koki-server acts as the core REST API backend in the Koki platform:

```
┌──────────────────────────────────────────────────────────────────┐
│                         External Clients                         │
│  - koki-portal (Admin UI)  - koki-portal-public (Public UI)     │
│  - koki-sdk (Client Library) - Chatbots - Mobile Apps           │
└────────────────────────────┬─────────────────────────────────────┘
                             │ HTTP/REST (JSON)
                             │ X-Tenant-ID Header + JWT
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
│  │  /v1/refdata  /v1/modules   /v1/roles      /v1/invitations│  │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │              Business Services Layer                     │   │
│  │  - AccountService     - ListingService   - LeadService   │   │
│  │  - OfferService       - MessageService   - FileService   │   │
│  │  - AuthenticationService  - EmailService - AiService     │   │
│  │  - TenantService      - ContactService   - AgentService  │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │          Data Access Layer (Spring Data JPA)             │   │
│  │  - AccountRepository  - ListingRepository - DAO Pattern  │   │
│  │  - LeadRepository     - OfferRepository   - Transactions │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         koki-platform (Infrastructure Library)           │   │
│  │  - Storage Provider (S3/Local)  - Cache (Redis)          │   │
│  │  - Message Queue (RabbitMQ)     - Logging & Monitoring   │   │
│  │  - Event Publishing             - Configuration          │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────────┬─────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐    ┌───────────────┐    ┌──────────────┐
│  MySQL 9.0    │    │  Redis 7.0+   │    │ RabbitMQ 4.0+│
│  (Primary DB) │    │  (Cache)      │    │ (Message Q)  │
│               │    │               │    │              │
│  - Tenants    │    │  - Sessions   │    │  - Events    │
│  - Users      │    │  - Ref Data   │    │  - Jobs      │
│  - Accounts   │    │  - Temp Data  │    │  - Notifs    │
│  - Listings   │    └───────────────┘    └──────────────┘
│  - Leads      │
│  - Offers     │
│  - Files      │
└───────┬───────┘
        │
        ▼
┌───────────────┐
│  Flyway       │
│  (Migrations) │
│  16 versions  │
└───────────────┘
```

**Data Flow:**

1. **Request Entry**: Clients send HTTP requests with `X-Tenant-ID` header and JWT Bearer token for authentication.
2. **Authentication**: Spring Security validates JWT, extracts user context, and verifies permissions.
3. **Tenant Isolation**: TenantContext ThreadLocal ensures all database queries are filtered by tenant ID.
4. **Business Logic**: Services orchestrate operations, enforce business rules, validate data, and coordinate with
   external systems.
5. **Data Persistence**: JPA repositories interact with MySQL, Flyway ensures schema consistency across environments.
6. **Caching**: Redis caches frequently accessed data (reference data, user sessions) to reduce database load.
7. **Async Processing**: RabbitMQ handles background tasks (email sending, notifications, metrics) for scalability.
8. **Storage**: Files uploaded via File API are stored in AWS S3 or local filesystem via koki-platform abstraction
   layer.
9. **Events**: Domain events are published to RabbitMQ for event-driven workflows and inter-service communication.

## API Reference

Interactive API documentation is available via Swagger UI:

[![LOCAL](https://img.shields.io/badge/Swagger-LOCAL-blue?logo=swagger)](http://localhost:8080/api.html)
[![TEST](https://img.shields.io/badge/Swagger-TEST-green?logo=swagger)](https://koki-server-test-71da83cfcf1a.herokuapp.com/api.html)

| Method             | Path                             | Description                             |
|--------------------|----------------------------------|-----------------------------------------|
| **Authentication** |
| POST               | `/v1/auth/login`                 | Authenticate user and receive JWT token |
| **Accounts**       |
| POST               | `/v1/accounts`                   | Create a new account                    |
| GET                | `/v1/accounts`                   | Search accounts with filters            |
| GET                | `/v1/accounts/{id}`              | Get account details by ID               |
| POST               | `/v1/accounts/{id}`              | Update account information              |
| DELETE             | `/v1/accounts/{id}`              | Delete an account                       |
| **Attributes**     |
| GET                | `/v1/attributes`                 | Get account attributes                  |
| POST               | `/v1/attributes/csv`             | Import attributes from CSV              |
| **Agents**         |
| GET                | `/v1/agents`                     | Search agents                           |
| GET                | `/v1/agents/{id}`                | Get agent details                       |
| **Contacts**       |
| POST               | `/v1/contacts`                   | Create a new contact                    |
| GET                | `/v1/contacts`                   | Search contacts with filters            |
| GET                | `/v1/contacts/{id}`              | Get contact details by ID               |
| POST               | `/v1/contacts/{id}`              | Update contact information              |
| DELETE             | `/v1/contacts/{id}`              | Delete a contact                        |
| **Files**          |
| POST               | `/v1/files/upload`               | Upload a file (multipart/form-data)     |
| GET                | `/v1/files`                      | Search files with filters               |
| GET                | `/v1/files/{id}`                 | Get file metadata                       |
| GET                | `/v1/files/{id}/content`         | Download file content                   |
| DELETE             | `/v1/files/{id}`                 | Delete a file                           |
| **Leads**          |
| POST               | `/v1/leads`                      | Create a new lead                       |
| GET                | `/v1/leads`                      | Search leads with filters               |
| GET                | `/v1/leads/{id}`                 | Get lead details by ID                  |
| POST               | `/v1/leads/{id}/status`          | Update lead status                      |
| **Listings**       |
| POST               | `/v1/listings`                   | Create a new property listing           |
| GET                | `/v1/listings`                   | Search listings with filters            |
| GET                | `/v1/listings/{id}`              | Get listing details by ID               |
| POST               | `/v1/listings/{id}`              | Update listing information              |
| POST               | `/v1/listings/{id}/amenities`    | Update listing amenities                |
| POST               | `/v1/listings/{id}/address`      | Update listing address                  |
| POST               | `/v1/listings/{id}/geo-location` | Update listing geo-location             |
| POST               | `/v1/listings/{id}/price`        | Update listing price                    |
| POST               | `/v1/listings/{id}/leasing`      | Update listing leasing info             |
| POST               | `/v1/listings/{id}/seller`       | Update listing seller                   |
| POST               | `/v1/listings/{id}/remarks`      | Update listing remarks                  |
| POST               | `/v1/listings/{id}/publish`      | Publish a listing                       |
| POST               | `/v1/listings/{id}/close`        | Close a listing                         |
| DELETE             | `/v1/listings/{id}`              | Delete a listing                        |
| **Messages**       |
| POST               | `/v1/messages`                   | Send a new message                      |
| GET                | `/v1/messages`                   | Search messages with filters            |
| GET                | `/v1/messages/{id}`              | Get message details by ID               |
| POST               | `/v1/messages/{id}/status`       | Update message status                   |
| **Modules**        |
| GET                | `/v1/modules`                    | Get all modules                         |
| GET                | `/v1/modules/{id}`               | Get module details by ID                |
| **Permissions**    |
| GET                | `/v1/permissions`                | Get all permissions                     |
| **Notes**          |
| POST               | `/v1/notes`                      | Create a new note                       |
| GET                | `/v1/notes`                      | Search notes with filters               |
| GET                | `/v1/notes/{id}`                 | Get note details by ID                  |
| POST               | `/v1/notes/{id}`                 | Update note content                     |
| DELETE             | `/v1/notes/{id}`                 | Delete a note                           |
| **Offers**         |
| POST               | `/v1/offers`                     | Create a new offer                      |
| GET                | `/v1/offers`                     | Search offers with filters              |
| GET                | `/v1/offers/{id}`                | Get offer details by ID                 |
| POST               | `/v1/offers/{id}`                | Update offer information                |
| DELETE             | `/v1/offers/{id}`                | Delete an offer                         |
| **Offer Versions** |
| GET                | `/v1/offer-versions`             | Get offer version history               |
| **Reference Data** |
| GET                | `/v1/amenities`                  | Get all amenities                       |
| GET                | `/v1/amenities/{id}`             | Get amenity details by ID               |
| GET                | `/v1/categories`                 | Get all categories                      |
| GET                | `/v1/categories/{id}`            | Get category details by ID              |
| GET                | `/v1/locations`                  | Search locations                        |
| GET                | `/v1/locations/{id}`             | Get location details by ID              |
| GET                | `/v1/refdata/types`              | Get reference data types                |
| **Tenants**        |
| GET                | `/v1/tenants`                    | Get all tenants                         |
| GET                | `/v1/tenants/{id}`               | Get tenant details by ID                |
| POST               | `/v1/tenants/{id}/init`          | Initialize tenant data                  |
| **Users**          |
| POST               | `/v1/users`                      | Create a new user                       |
| GET                | `/v1/users`                      | Search users with filters               |
| GET                | `/v1/users/{id}`                 | Get user details by ID                  |
| POST               | `/v1/users/{id}`                 | Update user information                 |
| DELETE             | `/v1/users/{id}`                 | Delete a user                           |
| **Roles**          |
| GET                | `/v1/roles`                      | Get all roles                           |
| GET                | `/v1/roles/{id}`                 | Get role details by ID                  |
| POST               | `/v1/roles/{id}/permissions`     | Update role permissions                 |
| **Invitations**    |
| POST               | `/v1/invitations`                | Invite a user to tenant                 |
| GET                | `/v1/invitations`                | Search invitations                      |
| GET                | `/v1/invitations/{id}`           | Get invitation details by ID            |
| POST               | `/v1/invitations/{id}/accept`    | Accept an invitation                    |
| POST               | `/v1/invitations/{id}/decline`   | Decline an invitation                   |
| **Configuration**  |
| GET                | `/v1/configurations`             | Get tenant configuration                |
| POST               | `/v1/configurations`             | Update tenant configuration             |

## License

This project is licensed under the MIT License. See the [LICENSE](../../LICENSE.md) file for details.

