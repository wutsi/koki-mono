# Koki Portal

Administrative web portal for the Koki multi-tenant real estate platform providing comprehensive property and account
management with server-side rendering.

[![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml)
[![pull_request](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml)

[![JaCoCo](https://github.com/wutsi/koki-mono/blob/master/.github/badges/koki-portal-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml)

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [High-Level System Diagram](#high-level-system-diagram)
- [License](#license)

## Features

- **Server-Side Rendered Web Application**: Thymeleaf-based web portal with full server-side rendering for fast page
  loads and SEO optimization
- **Multi-Tenant Administration**: Comprehensive tenant management with secure authentication and authorization
- **Property Listing Management**: Create, edit, publish, and manage property listings with rich media support
- **Account Management**: Full CRUD operations for accounts with custom attributes and bulk CSV import/export
- **Contact Management**: Centralized contact database with search and filtering capabilities
- **Lead Tracking**: Lead capture, status management, and tracking workflow
- **Offer Management**: Create and manage property offers with version control
- **File Management**: Upload, organize, and manage documents and media files
- **Message Center**: Internal messaging system with email integration
- **User Management**: Role-based access control with granular permissions
- **Settings Management**: Configurable email (SMTP), AI integration, and system settings
- **Responsive UI**: Mobile-friendly responsive design with modern UX patterns
- **Security Integration**: JWT-based authentication with Spring Security and session management
- **Form Validation**: Client-side and server-side validation with comprehensive error handling
- **Internationalization**: Multi-language support with resource bundles
- **Password Management**: Secure password reset and recovery workflows
- **User Signup**: Tenant invitation and user registration flows
- **Dashboard**: Customizable home dashboard with key metrics and quick actions
- **Share Functionality**: Built-in sharing capabilities for listings and content
- **Error Handling**: Custom error pages with user-friendly messaging

## Technologies

### Programming Languages

[![Kotlin](https://img.shields.io/badge/Kotlin-language-purple?logo=kotlin)](https://kotlinlang.org/)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://www.java.com/)
[![JavaScript](https://img.shields.io/badge/JavaScript-language-yellow?logo=javascript)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
[![HTML5](https://img.shields.io/badge/HTML5-markup-orange?logo=html5)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![CSS3](https://img.shields.io/badge/CSS3-styling-blue?logo=css3)](https://developer.mozilla.org/en-US/docs/Web/CSS)

### Frameworks

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-framework-green?logo=springsecurity)](https://spring.io/projects/spring-security)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-template%20engine-green?logo=thymeleaf)](https://www.thymeleaf.org/)

### Cloud

[![Heroku](https://img.shields.io/badge/Heroku-deployment-purple?logo=heroku)](https://www.heroku.com/)

### Tools & Libraries

[![Maven](https://img.shields.io/badge/Maven-build-red?logo=apachemaven)](https://maven.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-caching-red?logo=redis)](https://redis.io/)
[![JWT](https://img.shields.io/badge/JWT-authentication-black?logo=jsonwebtokens)](https://jwt.io/)
[![Selenium](https://img.shields.io/badge/Selenium-testing-green?logo=selenium)](https://www.selenium.dev/)

## High-Level Architecture

### Repository Structure

```
koki-portal/
├── src/main/kotlin/com/wutsi/koki/portal/
│   ├── Application.kt                    # Spring Boot application entry point
│   ├── account/                          # Account management pages and forms
│   │   └── page/                         # Account controllers and views
│   │       └── settings/                 # Account settings pages
│   ├── agent/                            # Agent management pages
│   ├── ai/                               # AI settings and configuration pages
│   ├── common/                           # Shared components and utilities
│   │   ├── page/                         # Base page controllers and models
│   │   └── service/                      # Common business services
│   ├── config/                           # Spring configuration classes
│   ├── contact/                          # Contact management pages
│   ├── email/                            # Email settings and configuration
│   ├── error/                            # Error handling and custom error pages
│   ├── file/                             # File upload and management pages
│   ├── forgot/                           # Password reset and recovery flows
│   ├── home/                             # Dashboard and home page
│   ├── lead/                             # Lead management pages
│   ├── listing/                          # Property listing management pages
│   ├── module/                           # Module and permission pages
│   ├── note/                             # Note management pages
│   ├── offer/                            # Offer management pages
│   ├── refdata/                          # Reference data pages (categories, locations)
│   ├── security/                         # Authentication and authorization
│   ├── settings/                         # Global settings pages
│   ├── share/                            # Sharing functionality
│   ├── signup/                           # User registration and signup flows
│   ├── tenant/                           # Tenant management and invitations
│   └── user/                             # User profile and management
├── src/main/resources/
│   ├── application.yml                   # Application configuration
│   ├── messages.properties               # Internationalization resource bundles
│   ├── layout/                           # Thymeleaf layout templates
│   ├── templates/                        # Thymeleaf page templates
│   └── public/                           # Static assets (CSS, JS, images)
└── pom.xml                               # Maven project configuration
```

### High-Level System Diagram

The Koki Portal follows a Model-View-Controller (MVC) architecture with server-side rendering:

```
┌─────────────────────────────────────────────────────────────┐
│                         Web Browser                          │
│                    (User Interface Layer)                    │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTPS
┌─────────────────────┴───────────────────────────────────────┐
│                      Koki Portal (MVC)                       │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Presentation Layer                       │  │
│  │  - Controllers (@Controller)                          │  │
│  │  - Form Validation                                    │  │
│  │  - Session Management                                 │  │
│  │  - Security Filters (Spring Security)                 │  │
│  └─────────────────────┬─────────────────────────────────┘  │
│                        │                                     │
│  ┌─────────────────────┴─────────────────────────────────┐  │
│  │              View Layer (Thymeleaf)                   │  │
│  │  - HTML Templates                                     │  │
│  │  - Layout Fragments                                   │  │
│  │  - Server-Side Rendering                              │  │
│  │  - Static Assets (CSS, JS, Images)                    │  │
│  └─────────────────────┬─────────────────────────────────┘  │
│                        │                                     │
│  ┌─────────────────────┴─────────────────────────────────┐  │
│  │              Service Layer                            │  │
│  │  - Business Logic                                     │  │
│  │  - API Client (Koki SDK)                              │  │
│  │  - Data Transformation                                │  │
│  └─────────────────────┬─────────────────────────────────┘  │
└────────────────────────┼───────────────────────────────────┘
                         │
          ┌──────────────┴──────────────┐
          │                             │
┌─────────┴──────────┐        ┌────────┴─────────┐
│   Koki Server API  │        │  Redis Cache     │
│   (Backend REST)   │        │  - Sessions      │
│   - Business Logic │        │  - Data Cache    │
│   - Data Storage   │        └──────────────────┘
└────────────────────┘
```

**Data Flow:**

1. **Request**: User interacts with web interface (form submission, navigation)
2. **Authentication**: Spring Security validates session and JWT tokens
3. **Controller**: Request routed to appropriate controller based on URL mapping
4. **Service Layer**: Controller delegates to service layer for business logic
5. **API Communication**: Service layer calls Koki Server REST API via SDK client
6. **Data Processing**: Response data transformed and prepared for view
7. **View Rendering**: Thymeleaf processes templates with model data on server
8. **Response**: Rendered HTML sent back to browser with embedded data
9. **Caching**: Redis stores session data and frequently accessed information

**Key Architectural Patterns:**

- **MVC Pattern**: Separation of concerns between controllers, views, and business logic
- **Server-Side Rendering**: Full HTML generation on server for fast initial loads and SEO
- **Template Engine**: Thymeleaf provides dynamic HTML templating with Spring integration
- **API Gateway Pattern**: Portal acts as API gateway, orchestrating calls to backend services
- **Session Management**: Redis-backed sessions for scalability and persistence
- **Security Layer**: Spring Security handles authentication, authorization, and CSRF protection
- **Form Handling**: Spring MVC form binding with validation and error handling
- **Responsive Design**: Mobile-first CSS with progressive enhancement

## License

This project is licensed under the MIT License - see the [LICENSE](../../LICENSE.md) file for details.

