# koki-portal

Server-side rendered administrative web application for the Koki platform, providing comprehensive management interfaces
for accounts, listings, leads, messages, files, and tenant operations.

[![koki-portal-master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml)
[![koki-portal-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml)
[![Code Coverage](../../.github/badges/koki-portal-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml)

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
- [Page Reference](#page-reference)
- [License](#license)

## Features

- **Server-Side Rendered UI**: Thymeleaf templates for dynamic HTML generation with SEO benefits and fast initial page
  loads
- **Session-Based Authentication**: Secure login flows with JWT token handling and HTTP-only cookies
- **Multi-Tenant Support**: Tenant-aware navigation, data isolation, and tenant switching capabilities
- **Comprehensive Management Pages**: Web interfaces for all Koki platform features:
    - **Accounts**: Create, view, edit, and search accounts with custom attributes
    - **Contacts**: Manage contact information with phone numbers and emails
    - **Listings**: Property listing management with photos, amenities, pricing, and location
    - **Leads**: Lead tracking with status workflows and assignment
    - **Messages**: Internal messaging system with threading
    - **Files**: File upload, download, and management with preview capabilities
    - **Offers**: Offer creation and management with versioning
    - **Notes**: Note-taking functionality linked to entities
    - **Agents**: Agent profile management with performance metrics
    - **Tenants**: Tenant configuration and settings
    - **Users**: User management, roles, permissions, and invitations
    - **Modules**: Enable/disable platform modules per tenant
- **Form Handling**: Robust form processing with validation, error handling, and user feedback
- **Responsive Design**: Mobile-friendly interfaces using modern CSS and Bootstrap
- **Internationalization**: Multi-language support with message bundles (English, French)
- **Integration via SDK**: Seamless communication with koki-server REST APIs through koki-sdk client
- **Security**: Spring Security integration with role-based access control (RBAC)
- **Error Pages**: Custom error pages for 404, 403, 500, and suspended tenants
- **File Upload**: Large file support (up to 50MB) with multipart form data
- **Actuator Endpoints**: Health checks and monitoring endpoints for operations
- **Feature Toggles**: Configure enabled/disabled features per environment

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple?logo=kotlin) ![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Frameworks

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green?logo=springsecurity) ![Spring MVC](https://img.shields.io/badge/Spring%20MVC-6.x-green?logo=spring) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green?logo=thymeleaf)

### Front-End

![HTML5](https://img.shields.io/badge/HTML5-Templates-orange?logo=html5) ![CSS3](https://img.shields.io/badge/CSS3-Styling-blue?logo=css3) ![Bootstrap](https://img.shields.io/badge/Bootstrap-5.x-purple?logo=bootstrap) ![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow?logo=javascript)

### Tools & Libraries

![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven) ![Selenium](https://img.shields.io/badge/Selenium-Testing-green?logo=selenium) ![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5) ![Mockito](https://img.shields.io/badge/Mockito-Mocking-yellow)

## Getting Started

### Prerequisites

Before running the project, ensure you have:

- **Java 17** or higher
- **Maven 3.8+** for dependency management
- **koki-server**: A running instance of koki-server (locally or remote)
- **Redis** (optional but recommended for session management in production)
- **Web Browser**: Modern browser supporting HTML5 and JavaScript

### Installation

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-portal
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

#### Basic Configuration

Update `application.yml` or create `application-local.yml` for local development:

```yaml
server:
    port: 8081

koki:
    webapp:
        client-id: koki-portal
        base-url: http://localhost:8081
        asset-url: ""
    sdk:
        base-url: http://localhost:8080  # koki-server URL
    toggles:
        modules:
            agent: true
            account: true
            contact: true
            listing: true
            message: true
            offer: true
```

#### Environment Variables

Key environment variables for production:

- `APP_PROFILE`: Spring profile to activate (e.g., `prod`, `test`)
- `PORT`: Server port (default: 8081)
- `JAVA_OPTS`: JVM options for memory and performance tuning
- `KOKI_SDK_BASE_URL`: Base URL for koki-server REST API
- `REDIS_URL`: Redis connection URL (if using Redis for sessions)

### Running the Project

#### Local Development

Run the application using Maven:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/koki-portal.jar
```

The portal will start on `http://localhost:8081`.

#### Access the Portal

Once running, access the portal in your web browser:

```
http://localhost:8081
```

**Default Login**: Use credentials configured in your koki-server instance.

#### Verify Health

Check if the service is running:

```bash
curl http://localhost:8081/actuator/health
```

Expected response:

```json
{
    "status": "UP"
}
```

#### Production Deployment

For production deployment (e.g., Heroku), the `Procfile` is configured:

```
web: java $JAVA_OPTS -Dserver.port=$PORT -jar koki-portal.jar --spring.profiles.active=$APP_PROFILE
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

#### Selenium Integration Tests

The portal includes Selenium-based UI tests (96 test files). To run integration tests:

```bash
mvn verify
```

**Note**: Selenium tests require a compatible web driver (ChromeDriver, GeckoDriver) installed on your system.

## High-Level Architecture

### Repository Structure

```
koki-portal/
├── pom.xml                          # Maven project configuration
├── Procfile                         # Heroku deployment configuration
├── system.properties                # Java version specification
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/wutsi/koki/portal/
│   │   │       ├── Application.kt   # Spring Boot main application
│   │   │       ├── account/         # Account management pages
│   │   │       │   ├── page/        # Controller classes for account pages
│   │   │       │   ├── model/       # View models and forms
│   │   │       │   └── service/     # Business logic for account operations
│   │   │       ├── agent/           # Agent management pages
│   │   │       ├── ai/              # AI settings and configuration pages
│   │   │       ├── common/          # Shared utilities and base controllers
│   │   │       ├── config/          # Spring configuration classes
│   │   │       ├── contact/         # Contact management pages
│   │   │       ├── email/           # Email-related pages
│   │   │       ├── error/           # Error pages (404, 403, 500, suspended)
│   │   │       ├── file/            # File upload/download pages
│   │   │       ├── forgot/          # Password/username recovery pages
│   │   │       ├── home/            # Dashboard and home page
│   │   │       ├── lead/            # Lead management pages
│   │   │       ├── listing/         # Property listing pages
│   │   │       ├── message/         # Messaging pages
│   │   │       ├── module/          # Module management pages
│   │   │       ├── note/            # Note management pages
│   │   │       ├── offer/           # Offer management pages
│   │   │       ├── refdata/         # Reference data pages (categories, amenities)
│   │   │       ├── security/        # Authentication and security
│   │   │       ├── settings/        # Settings and configuration pages
│   │   │       ├── share/           # Share functionality (social, email)
│   │   │       ├── signup/          # User signup and onboarding
│   │   │       ├── tenant/          # Tenant management pages
│   │   │       ├── translation/     # Translation service integration
│   │   │       └── user/            # User management pages
│   │   └── resources/
│   │       ├── application.yml      # Default configuration
│   │       ├── application-test.yml # Test configuration
│   │       ├── application-prod.yml # Production configuration
│   │       ├── messages.properties  # English message bundle
│   │       ├── messages_en.properties # English messages
│   │       ├── logback.xml          # Logging configuration
│   │       ├── public/              # Static assets (CSS, JS, images)
│   │       ├── templates/           # Thymeleaf HTML templates
│   │       │   ├── layout/          # Layout templates (header, footer, nav)
│   │       │   ├── account/         # Account page templates
│   │       │   ├── contact/         # Contact page templates
│   │       │   ├── listing/         # Listing page templates
│   │       │   ├── lead/            # Lead page templates
│   │       │   ├── message/         # Message page templates
│   │       │   ├── file/            # File page templates
│   │       │   ├── offer/           # Offer page templates
│   │       │   ├── user/            # User page templates
│   │       │   ├── tenant/          # Tenant page templates
│   │       │   ├── error/           # Error page templates
│   │       │   └── ...              # Other domain templates
│   │       └── layout/              # Additional layout resources
│   └── test/
│       └── kotlin/
│           └── com/wutsi/koki/portal/ # Unit and Selenium integration tests (96 test files)
└── target/                          # Build output directory
```

**Key Components:**

- **Application.kt**: Spring Boot entry point with `@KokiApplication` annotation for platform integration
- **Page Controllers** (254 Kotlin files): Spring MVC `@Controller` classes handling HTTP requests and rendering
  Thymeleaf templates
- **View Models**: Kotlin data classes representing form data and page models
- **Service Layer**: Business logic for data transformation and SDK client orchestration
- **Thymeleaf Templates**: HTML templates with Thymeleaf expressions for dynamic content rendering
- **Static Assets**: CSS, JavaScript, and image files served from `public/` directory
- **Configuration**: Spring configuration classes for security, SDK clients, and feature toggles
- **Internationalization**: Message bundles for multi-language support

### High-Level System Diagram

The koki-portal acts as the primary administrative interface for the Koki platform:

```
┌──────────────────────────────────────────────────────────────────┐
│                    Administrative Users                          │
│  - Tenant Admins    - Property Managers    - Support Staff      │
└────────────────────────────┬─────────────────────────────────────┘
                             │ Web Browser (HTTPS)
                             │ Session Cookie
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                         koki-portal                              │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │         Spring Security + Session Management             │   │
│  │  - Login Form  - JWT Token Handling  - RBAC             │   │
│  │  - HTTP-Only Cookies  - CSRF Protection                 │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         Spring MVC Controllers (@Controller)             │   │
│  │  - Home  - Accounts  - Listings  - Leads  - Messages    │   │
│  │  - Contacts  - Files  - Offers  - Users  - Tenants      │   │
│  │  - Settings  - Modules  - Agents  - Notes               │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         Thymeleaf Template Engine                        │   │
│  │  - Server-Side Rendering  - Layout Inheritance          │   │
│  │  - Expression Language  - Fragments                      │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         koki-sdk (REST Client Library)                   │   │
│  │  - KokiAuthentication  - KokiAccounts  - KokiListings   │   │
│  │  - KokiLeads  - KokiFiles  - KokiUsers  - KokiTenants   │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         koki-platform (Infrastructure Support)           │   │
│  │  - TenantProvider  - AccessTokenHolder  - Logging       │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────────┬─────────────────────────────────────┘
                             │ HTTP/REST (JSON)
                             │ Headers: X-Tenant-ID, Authorization
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                         koki-server                              │
│                      REST API Backend                            │
│  /v1/auth      /v1/accounts    /v1/listings   /v1/leads         │
│  /v1/files     /v1/messages    /v1/tenants    /v1/users         │
│  /v1/contacts  /v1/offers      /v1/notes      /v1/agents        │
└──────────────────────────────────────────────────────────────────┘
```

**Data Flow:**

1. **User Access**: Administrative users access the portal via web browser with session cookies
2. **Authentication**: Spring Security validates session and JWT token, enforces RBAC
3. **Request Handling**: Spring MVC controllers process HTTP requests (GET for pages, POST for forms)
4. **Business Logic**: Controllers invoke service layer methods to prepare data and validate input
5. **SDK Communication**: Services use koki-sdk clients to communicate with koki-server REST APIs
6. **Template Rendering**: Thymeleaf engine renders HTML templates with model data
7. **Response**: HTML pages sent to browser with embedded CSS/JavaScript
8. **User Interaction**: Forms submitted trigger POST requests, controller redirects on success

**Benefits:**

- **SEO-Friendly**: Server-side rendering enables search engine indexing
- **Fast Initial Load**: Pre-rendered HTML reduces client-side processing
- **Progressive Enhancement**: Works without JavaScript, enhanced with JS
- **Security**: Session management and CSRF protection built-in
- **Maintainability**: Clear separation between presentation (templates) and logic (controllers)

## Page Reference

The koki-portal provides web interfaces organized by business domain. Each domain includes pages for listing, viewing
details, creating, editing, and managing entities.

### Available Page Groups

| Group              | Description                 | Key Pages                                                                                                            |
|--------------------|-----------------------------|----------------------------------------------------------------------------------------------------------------------|
| **Home**           | Dashboard and overview      | Dashboard, Activity Feed                                                                                             |
| **Authentication** | Login and password recovery | Login, Forgot Password, Forgot Username, Reset Password                                                              |
| **Accounts**       | Account management          | List Accounts, View Account, Create Account, Edit Account, Account Selector                                          |
| **Contacts**       | Contact management          | List Contacts, View Contact, Create Contact, Edit Contact, Contact Selector                                          |
| **Listings**       | Property listing management | List Listings, View Listing, Create Listing, Edit Listing, Edit Amenities, Edit Address, Edit Pricing, Close Listing |
| **Leads**          | Lead tracking               | List Leads, View Lead, Edit Lead Status                                                                              |
| **Messages**       | Messaging system            | List Messages, View Message, Compose Message                                                                         |
| **Files**          | File management             | List Files, Upload File, View File, Download File                                                                    |
| **Offers**         | Offer management            | List Offers, View Offer, Create Offer, Edit Offer                                                                    |
| **Notes**          | Note management             | List Notes, Create Note, Edit Note, Delete Note                                                                      |
| **Agents**         | Agent management            | List Agents, View Agent, Edit Agent Profile                                                                          |
| **Users**          | User management             | List Users, View User, Create User, Edit User, Edit Profile, Edit Photo, User Invitations                            |
| **Tenants**        | Tenant management           | List Tenants, View Tenant, Tenant Settings, Tenant Modules                                                           |
| **Modules**        | Module configuration        | List Modules, Enable/Disable Modules                                                                                 |
| **Settings**       | Configuration               | General Settings, AI Settings, Translation Settings, Payment Settings                                                |
| **Error**          | Error pages                 | 404 Not Found, 403 Access Denied, 500 Server Error, Suspended Tenant                                                 |

### Common Page Patterns

#### List Pages

Display paginated lists with search, filtering, and sorting:

```
GET /accounts        # List all accounts
GET /contacts        # List all contacts
GET /listings        # List all property listings
GET /leads           # List all leads
GET /users           # List all users
```

#### Detail Pages

Display full details of a single entity:

```
GET /accounts/{id}   # View account details
GET /contacts/{id}   # View contact details
GET /listings/{id}   # View listing details
GET /leads/{id}      # View lead details
```

#### Create Pages

Forms for creating new entities:

```
GET  /accounts/create     # Show create account form
POST /accounts/create     # Submit new account
GET  /listings/create     # Show create listing form
POST /listings/create     # Submit new listing
```

#### Edit Pages

Forms for updating existing entities:

```
GET  /accounts/{id}/edit  # Show edit account form
POST /accounts/{id}/edit  # Submit account updates
GET  /listings/{id}/edit  # Show edit listing form
POST /listings/{id}/edit  # Submit listing updates
```

### Example User Flow

**Creating a New Listing:**

1. Navigate to `/listings` (List Listings page)
2. Click "Create Listing" button → redirects to `/listings/create`
3. Fill out listing form (title, property type, price, location)
4. Click "Save" → POST `/listings/create`
5. On success, redirect to `/listings/{id}` (View Listing page)
6. Click "Edit" → redirects to `/listings/{id}/edit` for updates
7. Update amenities via `/listings/{id}/amenities/edit`
8. Update address via `/listings/{id}/address/edit`

## License

This project is licensed under the MIT License. See the [LICENSE](../../LICENSE.md) file for details.
