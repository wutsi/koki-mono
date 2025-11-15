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
    - [View Layer](#view-layer)
    - [Tracking Integration](#tracking-integration)
    - [Configuration](#configuration)
- [Data Stores](#data-stores)
- [Deployment & Infrastructure](#deployment--infrastructure)
- [Security Considerations](#security-considerations)

## Overview

This document describes the architecture of the **koki-portal-public** module. The service is a public-facing Spring
Boot web application providing server-side rendered interfaces for end users to browse listings, access shared files,
view reference data, and interact with public content. Unlike the administrative koki-portal, this module focuses on
unauthenticated or lightly authenticated public access, implementing tracking for analytics, caching for performance,
and SEO-optimized rendering.

## Project Structure

```
modules/koki-portal-public/
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/wutsi/koki/portal/pub/
    │   │       ├── Application.kt
    │   │       ├── common/
    │   │       ├── config/
    │   │       ├── error/
    │   │       ├── file/
    │   │       │   ├── page/
    │   │       │   ├── service/
    │   │       │   ├── model/
    │   │       │   └── mapper/
    │   │       ├── home/
    │   │       ├── listing/
    │   │       │   ├── page/
    │   │       │   ├── service/
    │   │       │   ├── model/
    │   │       │   └── mapper/
    │   │       ├── refdata/
    │   │       ├── share/
    │   │       ├── tenant/
    │   │       ├── tracking/
    │   │       └── user/
    │   └── resources/
    │       ├── application.yml
    │       ├── messages.properties
    │       ├── public/
    │       └── templates/
    └── test/
```

## High-Level System Diagram

```
Public Web Users
        |
        v
  Spring Security Filter Chain
        |  Lightweight/No Authentication
        v
  Page Controllers (MVC)
        |
        v
  Service Layer
        |
        v
  Koki SDK Client
        |  REST API calls
        v
  Koki Server (Backend API)

Async Tracking:
  Page Controllers → Tracking Events → RabbitMQ → Tracking Server

Caching:
  Service Layer ← Redis Cache (frequently accessed data)

File Access:
  Users → File Controller → Local/S3 Storage
```

## Core Components

### Application Bootstrap

**Purpose:** Entry point for the Spring Boot web application with servlet scanning and AOP support.
**Key Functions:** Initializes Spring MVC, enables aspect-oriented programming for cross-cutting concerns, configures
servlet components.
**Interactions:** Scans and initializes all Spring beans, establishes HTTP server, and configures the application
context.

### Security Layer

**Purpose:** Implements lightweight security for public access scenarios.
**Key Functions:** Minimal authentication for public pages, optional user tracking without login, CSRF protection for
forms, rate limiting consideration.
**Interactions:** Intercepts incoming HTTP requests, allows public access while providing security baseline.

### Page Controllers

**Purpose:** Handle HTTP requests and return server-side rendered HTML pages for public consumption.
**Components & Functions:**

- **Home Pages**: Landing pages, navigation, and public dashboard
- **Listing Pages**: Public product/service listing browsing, search, and detail views
- **File Pages**: Access to shared files and documents via public links
- **Share Pages**: Shared content access and viewing
- **RefData Pages**: Public reference data display (categories, locations, amenities)
- **Tenant Pages**: Tenant-specific public branding and content
- **User Pages**: Public user profiles and directories
- **Tracking Pages**: Analytics and tracking endpoint handling
  **Interactions:** Receive HTTP requests, invoke service layer, populate model objects, publish tracking events, and
  return Thymeleaf template names.

### Service Layer

**Purpose:** Encapsulates business logic for public-facing operations.
**Key Functions:** Data retrieval via SDK client, caching frequently accessed data, data transformation for views,
tracking event generation, validation.
**Interactions:** Called by page controllers, uses koki-sdk to communicate with koki-server, publishes events to
RabbitMQ for tracking, leverages Redis cache.

### View Layer

**Purpose:** Server-side HTML rendering using Thymeleaf templates optimized for SEO.
**Key Functions:** Dynamic page generation with semantic HTML, template fragments for reusability, mobile-responsive
layouts, internationalization support, structured data markup.
**Interactions:** Receives model data from controllers, applies Thymeleaf expressions, generates SEO-friendly HTML
responses.

### Tracking Integration

**Purpose:** Capture user interaction events for analytics.
**Key Functions:** Page view tracking, listing impression tracking, interaction event generation, asynchronous event
publishing to RabbitMQ.
**Interactions:** Controllers generate tracking events, events published to message queue for processing by
koki-tracking-server.

### Configuration

**Purpose:** Centralizes application configuration and bean definitions.
**Key Functions:** Koki SDK client configuration, Redis cache setup, RabbitMQ tracking configuration, Thymeleaf
configuration, internationalization, security policies.
**Interactions:** Provides configuration beans consumed by other components.

## Data Stores

- **Redis Cache**: Distributed cache for frequently accessed public data (listings, reference data, tenant information).
  Reduces backend API calls and improves response times.
- **RabbitMQ**: Message broker for asynchronous tracking event delivery. Events published to tracking exchange for
  processing.
- **File Storage (Local or S3)**: Storage for publicly accessible files accessed via koki-server.

Note: The portal does not directly access any database. All data operations are performed via koki-server REST APIs
through the SDK client.

## Deployment & Infrastructure

Build:

```bash
mvn clean install
```

Artifact: Executable JAR `koki-portal-public-VERSION_NUMBER.jar`.

Runtime Profiles:

- **local**: Local development with local SDK endpoint and local storage.
- **test**: Testing environment connecting to test backend and Redis.
- **prod**: Production environment with production backend URL, Redis cluster, and S3 storage.

Key Configuration:

```yaml
server.port: 8082
koki.webapp.client-id: koki-portal-public
koki.sdk.base-url: [ BACKEND_API_URL ]
wutsi.platform.cache.type: redis
wutsi.platform.cache.redis.url: redis://[REDIS_HOST]:6379
wutsi.platform.mq.type: rabbitmq
wutsi.platform.mq.rabbitmq.url: amqp://[RABBIT_HOST]
wutsi.platform.storage.type: local | s3
```

Scaling:

- Stateless design with Redis-backed caching enables horizontal scaling.
- Multiple portal instances can share Redis cache and RabbitMQ.
- CDN integration recommended for static assets in production.

Observability:

- Actuator endpoints for health and application info.
- Structured logging with request context and tracking IDs.
- Error pages provide user-friendly messages while logging detailed errors.

## Security Considerations

- **Public Access**: Most pages accessible without authentication, optimized for SEO and discoverability.
- **Lightweight Authentication**: Optional user identification for personalization without requiring full login.
- **CSRF Protection**: Spring Security CSRF tokens included in any state-changing forms.
- **Input Validation**: Form validation to prevent malicious input.
- **XSS Prevention**: Thymeleaf automatic HTML escaping prevents cross-site scripting attacks.
- **Rate Limiting**: Application-level rate limiting recommended to prevent abuse.
- **Transport Security**: HTTPS required in production environments.
- **File Access Control**: Public file links validated and scoped appropriately.
- **Bot Protection**: Tracking system filters bot traffic for accurate analytics.
- **Error Handling**: Generic error pages prevent information leakage while maintaining user experience.

