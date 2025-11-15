# ARCHITECTURE

## Table of Content

- [Overview](#overview)
- [Project Structure](#project-structure)
- [High-Level System Diagram](#high-level-system-diagram)
- [Core Components](#core-components)
    - [Application Bootstrap](#application-bootstrap)
    - [Security Layer](#security-layer)
    - [Page Controllers](#page-controllers)
    - [Service Layer](#service-layer)
    - [Form Handlers](#form-handlers)
    - [View Layer](#view-layer)
    - [Configuration](#configuration)
- [Data Stores](#data-stores)
- [Deployment & Infrastructure](#deployment--infrastructure)
- [Security Considerations](#security-considerations)

## Overview

This document describes the architecture of the **koki-portal** module. The service is a Spring Boot web application
providing server-side rendered user interfaces for the Koki platform. It serves as the primary administrative portal,
offering web-based access to account management, contacts, listings, leads, messaging, files, and configuration. The
application uses Thymeleaf templates for server-side rendering, integrates with koki-server via SDK client, and
implements session-based authentication with JWT tokens.

## Project Structure

```
modules/koki-portal/
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/wutsi/koki/portal/
    │   │       ├── Application.kt
    │   │       ├── account/
    │   │       │   ├── page/
    │   │       │   ├── service/
    │   │       │   ├── form/
    │   │       │   ├── model/
    │   │       │   └── mapper/
    │   │       ├── agent/
    │   │       ├── ai/
    │   │       ├── common/
    │   │       ├── config/
    │   │       ├── contact/
    │   │       ├── email/
    │   │       ├── error/
    │   │       ├── file/
    │   │       ├── forgot/
    │   │       ├── home/
    │   │       ├── lead/
    │   │       ├── listing/
    │   │       ├── message/
    │   │       ├── module/
    │   │       ├── note/
    │   │       ├── offer/
    │   │       ├── refdata/
    │   │       ├── security/
    │   │       ├── settings/
    │   │       ├── share/
    │   │       ├── signup/
    │   │       ├── tenant/
    │   │       ├── translation/
    │   │       └── user/
    │   └── resources/
    │       ├── application.yml
    │       ├── messages.properties
    │       ├── layout/
    │       ├── public/
    │       └── templates/
    └── test/
```

## High-Level System Diagram

```
Web Browser (User)
        |
        v
  Spring Security Filter Chain
        |  Session-based Authentication
        |  CSRF Protection
        v
  Page Controllers (MVC)
        |
        v
  Service Layer
        |
        v
  Koki SDK Client
        |  REST API calls with JWT
        v
  Koki Server (Backend API)
        |
        v
  MySQL Database

File Storage:
  - Local Filesystem or S3

Cache (Optional):
  - Redis for session management
```

## Core Components

### Application Bootstrap

**Purpose:** Entry point for the Spring Boot web application with servlet scanning and AOP support.
**Key Functions:** Initializes Spring MVC, enables aspect-oriented programming for cross-cutting concerns, configures
servlet components.
**Interactions:** Scans and initializes all Spring beans, establishes HTTP server, and configures the application
context.

### Security Layer

**Purpose:** Implements authentication, authorization, and session management for web access.
**Key Functions:** JWT token handling, session management, login/logout flows, CSRF protection, role-based access
control for pages.
**Interactions:** Intercepts all incoming HTTP requests, validates user sessions, establishes security context, and
enforces access policies.

### Page Controllers

**Purpose:** Handle HTTP requests and return server-side rendered HTML pages.
**Components & Functions:**

- **Account Pages**: User profile, account settings, and management
- **Contact Pages**: Contact list, detail, creation, and editing
- **Listing Pages**: Product/service listing management interface
- **Lead Pages**: Lead pipeline, tracking, and management views
- **Message Pages**: Internal messaging interface and notifications
- **File Pages**: File upload, browsing, and management
- **Offer Pages**: Offer creation, editing, and versioning
- **Tenant Pages**: Tenant configuration and settings
- **Home Pages**: Dashboard and navigation
- **Settings Pages**: Application configuration and preferences
- **User Pages**: User management and role assignment
- **Signup/Forgot Pages**: Registration and password recovery flows
  **Interactions:** Receive HTTP requests, invoke service layer for business logic, populate model objects, and return
  Thymeleaf template names for rendering.

### Service Layer

**Purpose:** Encapsulates business logic and orchestrates interactions with backend APIs.
**Key Functions:** Data transformation between forms and DTOs, validation logic, SDK client invocation, error handling,
and session state management.
**Interactions:** Called by page controllers, uses koki-sdk to communicate with koki-server, transforms data for view
presentation.

### Form Handlers

**Purpose:** Process form submissions and validation.
**Key Functions:** Form data binding, validation annotations processing, error message generation, and redirect logic.
**Interactions:** Receive POST requests from pages, validate input, delegate to service layer, and return success/error
responses.

### View Layer

**Purpose:** Server-side HTML rendering using Thymeleaf templates.
**Key Functions:** Dynamic page generation, template fragments for reusability, layout inheritance, internationalization
support.
**Interactions:** Receives model data from controllers, applies Thymeleaf expressions, and generates final HTML
response.

### Configuration

**Purpose:** Centralizes application configuration and bean definitions.
**Key Functions:** Koki SDK client configuration, security policies, Thymeleaf configuration, internationalization, file
upload limits, and error handling.
**Interactions:** Provides configuration beans consumed by other components.

## Data Stores

- **Session Store**: HTTP sessions for user authentication state. Can use in-memory sessions or Redis for distributed
  session management.
- **Redis Cache (Optional)**: Distributed cache for session replication across multiple instances.
- **File Storage (Local or S3)**: Storage for uploaded files accessed via koki-server.

Note: The portal does not directly access the database. All data operations are performed via koki-server REST APIs
through the SDK client.

## Deployment & Infrastructure

Build:

```bash
mvn clean install
```

Artifact: Executable JAR `koki-portal-VERSION_NUMBER.jar`.

Runtime Profiles:

- **local**: Local development with local SDK endpoint.
- **test**: Testing environment connecting to test backend.
- **prod**: Production environment with production backend URL.

Key Configuration:

```yaml
server.port: 8081
koki.webapp.client-id: [CLIENT_ID]
koki.server.url: [BACKEND_API_URL]
wutsi.platform.cache.type: none | redis
wutsi.platform.cache.redis.url: redis://[REDIS_HOST]:6379
wutsi.platform.storage.type: local | s3
```

Scaling:

- Stateless design (when using Redis sessions) allows horizontal scaling.
- Multiple portal instances can share Redis for session storage.
- Static assets served efficiently via Spring Boot embedded server.

Observability:

- Actuator endpoints for health and application info.
- Structured logging with request context.
- Error pages for user-friendly error handling.

## Security Considerations

- **Authentication**: Session-based authentication with JWT tokens stored in HTTP-only cookies. Login handled through
  koki-server authentication endpoints.
- **Authorization**: Role-based page access controlled via Spring Security. Users redirected to login page when
  unauthenticated.
- **CSRF Protection**: Spring Security CSRF tokens included in all state-changing forms.
- **Session Management**: Secure session configuration with timeout policies. Redis session replication for
  multi-instance deployments.
- **Input Validation**: Form validation using Bean Validation annotations and custom validators.
- **XSS Prevention**: Thymeleaf automatic HTML escaping prevents cross-site scripting attacks.
- **Transport Security**: HTTPS required in production environments.
- **Secrets Management**: API credentials and JWT secrets externalized via environment variables.
- **File Upload Security**: File size limits enforced, file type validation recommended.
- **Error Handling**: Generic error pages prevent information leakage while logging detailed errors server-side.

