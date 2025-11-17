# koki-portal-public

Public-facing web application for the Koki platform, providing property listing discovery, search, and viewing
interfaces for end-users and potential customers.

[![koki-portal-public-master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml)
[![koki-portal-public-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml)
[![Code Coverage](../../.github/badges/koki-portal-public-jacoco.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml)

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [High-Level System Diagram](#high-level-system-diagram)
- [Page Reference](#page-reference)
- [License](#license)

## Features

- **Server-Side Rendered UI**: Thymeleaf templates for SEO-optimized property listing pages
- **Property Listing Discovery**: Browse and search property listings with advanced filtering
- **Listing Detail Pages**: Comprehensive property information with photo galleries, amenities, and location maps
- **Responsive Design**: Mobile-first design optimized for smartphones, tablets, and desktops
- **SEO Optimization**: Semantic HTML, meta tags, structured data for search engine visibility
- **Performance Optimized**: Fast page loads with caching, CDN-ready static assets, and image optimization
- **Multi-Tenant Support**: Tenant-aware routing and branding
- **Social Sharing**: Share listings via social media, email, and messaging apps
- **Image Galleries**: Lightbox image viewing with full-screen support
- **Property Search**: Filter by location, price range, property type, amenities, and more
- **Tracking Integration**: User activity tracking for analytics and attribution
- **PWA Support**: Progressive Web App capabilities with manifest and service worker ready
- **Error Pages**: User-friendly error pages for 404, 500, and suspended tenants
- **Redis Caching**: High-performance caching for listing data and static content
- **Internationalization Ready**: Multi-language support structure in place

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple?logo=kotlin) ![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Frameworks

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green?logo=springsecurity) ![Spring MVC](https://img.shields.io/badge/Spring%20MVC-6.x-green?logo=spring) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green?logo=thymeleaf)

### Front-End

![HTML5](https://img.shields.io/badge/HTML5-Templates-orange?logo=html5) ![CSS3](https://img.shields.io/badge/CSS3-Styling-blue?logo=css3) ![Bootstrap](https://img.shields.io/badge/Bootstrap-5.x-purple?logo=bootstrap) ![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow?logo=javascript)

### Infrastructure

![Redis](https://img.shields.io/badge/Redis-Cache-red?logo=redis) ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4.2-orange?logo=rabbitmq)

### Tools & Libraries

![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven) ![Selenium](https://img.shields.io/badge/Selenium-Testing-green?logo=selenium) ![JUnit](https://img.shields.io/badge/JUnit-5-green?logo=junit5) ![Mockito](https://img.shields.io/badge/Mockito-Mocking-yellow)

## High-Level Architecture

### Repository Structure

```
koki-portal-public/
├── pom.xml                          # Maven project configuration
├── Procfile                         # Heroku deployment configuration
├── system.properties                # Java version specification
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/wutsi/koki/portal/pub/
│   │   │       ├── Application.kt   # Spring Boot main application
│   │   │       ├── common/          # Shared utilities and base controllers
│   │   │       │   ├── page/        # Base page controllers
│   │   │       │   ├── model/       # Common view models
│   │   │       │   └── service/     # Shared business logic
│   │   │       ├── config/          # Spring configuration classes
│   │   │       │   ├── SecurityConfiguration.kt
│   │   │       │   ├── SdkConfiguration.kt
│   │   │       │   └── CacheConfiguration.kt
│   │   │       ├── error/           # Error pages (404, 500, suspended)
│   │   │       │   └── page/        # Error page controllers
│   │   │       ├── file/            # File and image handling
│   │   │       │   └── page/        # Image modal, lightbox
│   │   │       ├── home/            # Home page and PWA manifest
│   │   │       │   └── page/        # Home controller, manifest
│   │   │       ├── listing/         # Property listing pages
│   │   │       │   ├── page/        # Listing detail, search controllers
│   │   │       │   ├── model/       # Listing view models
│   │   │       │   └── service/     # Listing business logic
│   │   │       ├── refdata/         # Reference data (categories, locations)
│   │   │       │   ├── page/        # Reference data controllers
│   │   │       │   └── service/     # Reference data services
│   │   │       ├── share/           # Social sharing functionality
│   │   │       │   └── page/        # Share modal controller
│   │   │       ├── tenant/          # Tenant context and routing
│   │   │       │   ├── filter/      # Tenant resolution filters
│   │   │       │   └── service/     # Tenant services
│   │   │       ├── tracking/        # Analytics and tracking
│   │   │       │   └── page/        # Tracking pixel controller
│   │   │       └── user/            # User context (optional auth)
│   │   │           └── service/     # User services
│   │   └── resources/
│   │       ├── application.yml      # Default configuration
│   │       ├── application-test.yml # Test configuration
│   │       ├── application-prod.yml # Production configuration
│   │       ├── logback.xml          # Logging configuration
│   │       ├── public/              # Static assets (CSS, JS, images)
│   │       │   ├── css/             # Stylesheets
│   │       │   ├── js/              # JavaScript files
│   │       │   ├── images/          # Image assets
│   │       │   └── favicon.ico      # Site favicon
│   │       └── templates/           # Thymeleaf HTML templates
│   │           ├── layout/          # Layout templates (header, footer, nav)
│   │           ├── home/            # Home page templates
│   │           ├── listing/         # Listing page templates
│   │           │   ├── list.html    # Listing search results
│   │           │   └── detail.html  # Listing detail page
│   │           ├── error/           # Error page templates
│   │           ├── share/           # Share modal templates
│   │           └── fragments/       # Reusable template fragments
│   └── test/
│       └── kotlin/
│           └── com/wutsi/koki/portal/pub/ # Unit and Selenium integration tests (14 test files)
└── target/                          # Build output directory
```

**Key Components:**

- **Application.kt**: Spring Boot entry point with `@KokiApplication` annotation for platform integration
- **Page Controllers** (53 Kotlin files): Spring MVC `@Controller` classes handling HTTP requests and rendering
  Thymeleaf templates
- **View Models**: Kotlin data classes representing page data optimized for public display
- **Service Layer**: Business logic for data transformation, caching, and SDK client orchestration
- **Thymeleaf Templates**: SEO-optimized HTML templates with structured data and meta tags
- **Static Assets**: CSS, JavaScript, and image files served from `public/` directory
- **Configuration**: Spring configuration for security (minimal auth), SDK clients, Redis caching
- **Redis Caching**: High-performance caching for listing data with 24-hour TTL

### High-Level System Diagram

The koki-portal-public acts as the public-facing interface for property discovery:

```
┌──────────────────────────────────────────────────────────────────┐
│                    Public Users / Visitors                       │
│  - Property Seekers   - Potential Buyers   - Search Engines     │
└────────────────────────────┬─────────────────────────────────────┘
                             │ Web Browser (HTTPS)
                             │ No Authentication Required
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                      koki-portal-public                          │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │         Tenant Resolution (Filter)                       │   │
│  │  - Subdomain Detection  - Custom Domain Routing         │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         Spring MVC Controllers (@Controller)             │   │
│  │  - Home  - Listing Search  - Listing Detail             │   │
│  │  - Share  - Tracking  - Manifest                         │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         Redis Cache (Listing Data)                       │   │
│  │  - Listing summaries  - Detail pages  - TTL: 24h        │   │
│  └────────────┬──────────────────────┬────────────────────┐   │
│               │ Cache Miss           │ Cache Hit           │   │
│               ▼                      │                     │   │
│  ┌────────────────────────┐          │                     │   │
│  │  koki-sdk Client       │          │                     │   │
│  │  - KokiListings        │          │                     │   │
│  │  - KokiRefData         │          │                     │   │
│  └────────────┬───────────┘          │                     │   │
│               │                      │                     │   │
│               ▼                      ▼                     │   │
│  ┌────────────────────────────────────────────────────────┐   │
│  │         Thymeleaf Template Engine                      │   │
│  │  - SEO Meta Tags  - Structured Data  - Open Graph     │   │
│  │  - Server-Side Rendering  - Image Optimization        │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         Tracking & Analytics                             │   │
│  │  - Page Views  - Click Events  - RabbitMQ Events       │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────────┬─────────────────────────────────────┘
                             │ HTTP/REST (JSON)
                             │ Headers: X-Tenant-ID
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                         koki-server                              │
│                      REST API Backend                            │
│  /v1/listings      /v1/refdata      /v1/files                   │
└──────────────────────────────────────────────────────────────────┘
```

**Data Flow:**

1. **User Access**: Public users access the portal via web browser (no authentication required)
2. **Tenant Resolution**: Tenant filter detects tenant from subdomain or custom domain
3. **Request Handling**: Spring MVC controllers process HTTP requests for listing pages
4. **Cache Check**: Redis cache checked for listing data (24-hour TTL)
5. **SDK Communication** (on cache miss): koki-sdk clients fetch data from koki-server
6. **Cache Update**: Fresh data stored in Redis for subsequent requests
7. **Template Rendering**: Thymeleaf engine renders SEO-optimized HTML with structured data
8. **Response**: HTML pages with meta tags, Open Graph, and Twitter Card data
9. **Tracking**: User interactions tracked and sent to analytics via RabbitMQ

**Benefits:**

- **SEO-Friendly**: Server-side rendering with proper meta tags and structured data
- **High Performance**: Redis caching reduces API calls and improves response times
- **Scalability**: Stateless design with caching enables horizontal scaling
- **Multi-Tenant**: Subdomain/domain-based tenant routing for white-label support
- **User Experience**: Fast page loads and responsive design for all devices

## Page Reference

The koki-portal-public provides public-facing pages for property listing discovery and viewing. All pages are publicly
accessible without authentication.

### Available Page Groups

| Group        | Description                  | Key Pages                                         |
|--------------|------------------------------|---------------------------------------------------|
| **Home**     | Landing page and overview    | Home Page, Featured Listings                      |
| **Listings** | Property listing discovery   | Listing Search, Listing Detail, Photo Gallery     |
| **Share**    | Social sharing functionality | Share Modal (Facebook, Twitter, Email, WhatsApp)  |
| **Tracking** | Analytics and tracking       | Tracking Pixel, Event Tracking                    |
| **PWA**      | Progressive Web App support  | Web Manifest                                      |
| **Error**    | Error pages                  | 404 Not Found, 500 Server Error, Suspended Tenant |

### Key Pages

#### Home Page

Landing page with featured listings and search functionality:

```
GET /                    # Home page with featured listings
```

**Features:**

- Featured property listings
- Search bar with quick filters
- Category browsing
- Location-based search
- Recent listings

#### Listing Pages

Property listing discovery and detail pages:

```
GET /listings                        # Listing search results page
GET /listings?location=...           # Filter by location
GET /listings?price_min=...          # Filter by price range
GET /listings?property_type=...      # Filter by property type
GET /listings/{id}                   # Listing detail page
```

**Listing Detail Page Features:**

- Property photos with lightbox gallery
- Property description and features
- Amenities list
- Location map
- Price and payment terms
- Contact information
- Social sharing buttons
- Related listings

#### Share Functionality

Social sharing modal for listings:

```
GET /share/modal?url=...             # Share modal with social buttons
```

**Supported Platforms:**

- Facebook
- Twitter / X
- LinkedIn
- WhatsApp
- Email
- Copy Link

#### Tracking

Analytics tracking pixel:

```
GET /track?event=...                 # Tracking pixel endpoint
```

**Tracked Events:**

- Page views
- Listing views
- Share actions
- Click events

#### PWA Support

Progressive Web App manifest:

```
GET /manifest.json                   # Web app manifest
```

**PWA Features:**

- Add to home screen
- Offline support (future)
- Push notifications (future)

### SEO Features

All listing pages include:

- **Meta Tags**: Title, description, keywords
- **Open Graph**: Facebook/LinkedIn sharing preview
- **Twitter Card**: Twitter sharing preview
- **Structured Data**: JSON-LD schema.org markup for search engines
- **Canonical URLs**: Proper URL canonicalization
- **Sitemap Ready**: Pages optimized for sitemap generation
- **Mobile Optimization**: Responsive design with viewport meta tags

### Example User Flow

**Searching for a Property:**

1. Navigate to `/` (Home page)
2. Enter search criteria (location, price, property type)
3. Submit search → redirects to `/listings?location=...&price_min=...`
4. Browse search results with pagination
5. Click on a listing → redirects to `/listings/{id}` (Listing detail)
6. View photos in lightbox gallery
7. Click "Share" → opens share modal
8. Share listing on social media or via email
9. Click on related listings to continue browsing

**Tracking Flow:**

1. User views listing detail page
2. Tracking pixel fires: `GET /track?event=view_listing&listing_id={id}`
3. Event logged to analytics system via RabbitMQ
4. Data available for reporting and attribution

## License

This project is licensed under the MIT License. See the [LICENSE](../../LICENSE.md) file for details.

