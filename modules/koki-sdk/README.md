# koki-sdk

Type-safe Kotlin client library for seamless integration with Koki REST APIs, providing domain-focused wrappers for
authentication, accounts, listings, offers, files, and tenant management.

[![koki-sdk-master](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml)
[![koki-sdk-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml)

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

- **Type-Safe API Wrappers**: Kotlin client classes with compile-time type checking for all Koki REST endpoints
- **Domain-Focused Modules**: Organized by business domains (Accounts, Listings, Offers, Files, Tenants, etc.)
- **Shared DTO Contracts**: Uses `koki-dto` for consistent request/response models across client and server
- **Automatic URL Building**: `URLBuilder` handles path construction, query parameter encoding, and collection
  serialization
- **Multi-Tenant Support**: Built-in tenant context propagation via `TenantProvider`
- **Authentication Integration**: Seamless JWT token management through `AccessTokenHolder`
- **File Upload Support**: Simplified multipart file upload with `AbstractKokiModule`
- **REST Template Integration**: Leverages Spring's `RestTemplate` for HTTP communication
- **Error Handling**: Standardized error envelope parsing for consistent exception handling
- **Pagination Support**: Built-in limit/offset parameters for search operations
- **Collection Parameters**: Automatic encoding of list parameters in query strings
- **Dependency Injection Ready**: Designed to work with Spring Boot's dependency injection

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple?logo=kotlin) ![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Frameworks

![Spring](https://img.shields.io/badge/Spring-6.x-green?logo=spring) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot)

### Tools & Libraries

![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven) ![Apache Commons IO](https://img.shields.io/badge/Commons%20IO-Utilities-red?logo=apache)

## High-Level Architecture

### Repository Structure

```
koki-sdk/
├── pom.xml                          # Maven project configuration
├── README.md                        # This documentation
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/wutsi/koki/sdk/
│       │       ├── AbstractKokiModule.kt       # Base class with file upload support
│       │       ├── URLBuilder.kt               # URL construction and query encoding
│       │       ├── KokiAuthentication.kt       # Authentication API client
│       │       ├── KokiAccounts.kt             # Account management API client
│       │       ├── KokiAgent.kt                # Agent management API client
│       │       ├── KokiConfiguration.kt        # Configuration API client
│       │       ├── KokiContacts.kt             # Contact management API client
│       │       ├── KokiFiles.kt                # File upload/download API client
│       │       ├── KokiInvitations.kt          # Invitation management API client
│       │       ├── KokiLead.kt                 # Lead management API client
│       │       ├── KokiListings.kt             # Listing management API client
│       │       ├── KokiMessages.kt             # Message API client
│       │       ├── KokiModules.kt              # Module management API client
│       │       ├── KokiNotes.kt                # Note management API client
│       │       ├── KokiOffer.kt                # Offer management API client
│       │       ├── KokiOfferVersion.kt         # Offer version API client
│       │       ├── KokiRefData.kt              # Reference data API client
│       │       ├── KokiRoles.kt                # Role management API client
│       │       ├── KokiTenants.kt              # Tenant management API client
│       │       ├── KokiTypes.kt                # Type management API client
│       │       └── KokiUsers.kt                # User management API client
│       └── resources/
└── target/                          # Build output directory
```

**Key Components:**

- **AbstractKokiModule**: Base class providing common functionality for all SDK modules, particularly multipart file
  upload support using Spring's `RestTemplate` and `MultipartFile`
- **URLBuilder**: Handles URL construction with path parameters and query string encoding, including automatic
  serialization of collection parameters
- **Domain Clients** (Koki*): Each class wraps a specific domain API (accounts, listings, files, etc.) with type-safe
  methods that:
    - Accept strongly-typed DTOs from `koki-dto`
    - Build appropriate URLs using `URLBuilder`
    - Execute HTTP requests via `RestTemplate`
    - Return typed responses

### High-Level System Diagram

The koki-sdk acts as a client-side abstraction layer for koki-server REST APIs:

```
┌──────────────────────────────────────────────────────────────────┐
│                    Client Applications                           │
│  - koki-portal (Admin UI)  - koki-portal-public (Public UI)     │
│  - External Integrations   - Chatbots   - Mobile Apps           │
└────────────────────────────┬─────────────────────────────────────┘
                             │ Application Code
                             │ (Dependency Injection)
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                         koki-sdk                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Domain-Specific SDK Clients                 │   │
│  │  - KokiAuthentication  - KokiAccounts   - KokiListings   │   │
│  │  - KokiOffers          - KokiFiles      - KokiLeads      │   │
│  │  - KokiTenants         - KokiUsers      - KokiMessages   │   │
│  │  - KokiContacts        - KokiNotes      - KokiAgent      │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         URLBuilder (Path + Query Construction)           │   │
│  │  - Base URL configuration                                │   │
│  │  - Query parameter encoding (including collections)      │   │
│  │  - URL-safe encoding (UTF-8)                             │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         Spring RestTemplate (HTTP Client)                │   │
│  │  - GET, POST, DELETE operations                          │   │
│  │  - Request/Response serialization (JSON)                 │   │
│  │  - Multipart file upload support                         │   │
│  │  - Interceptors for headers (Tenant-ID, Authorization)   │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         koki-dto (Shared Data Contracts)                 │   │
│  │  - Request DTOs  - Response DTOs  - Domain Models        │   │
│  └────────────────────────┬─────────────────────────────────┘   │
│                           │                                      │
│  ┌────────────────────────▼─────────────────────────────────┐   │
│  │         koki-platform (Infrastructure Support)           │   │
│  │  - TenantProvider     - AccessTokenHolder                │   │
│  │  - Multi-tenancy context                                 │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────────┬─────────────────────────────────────┘
                             │ HTTP/REST (JSON)
                             │ Headers: X-Tenant-ID, Authorization
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                         koki-server                              │
│                      REST API Backend                            │
│  /v1/auth      /v1/accounts    /v1/listings   /v1/offers        │
│  /v1/files     /v1/leads       /v1/tenants    /v1/users         │
│  /v1/messages  /v1/contacts    /v1/notes      /v1/agents        │
└──────────────────────────────────────────────────────────────────┘
```

**Data Flow:**

1. **Application Layer**: Client applications inject SDK domain clients (e.g., `KokiAccounts`, `KokiListings`)
2. **SDK Client Layer**: Domain clients provide type-safe methods that accept/return DTOs from `koki-dto`
3. **URL Construction**: `URLBuilder` constructs complete URLs with query parameters, handling collection encoding
4. **HTTP Communication**: Spring `RestTemplate` executes HTTP requests with:
    - Automatic JSON serialization/deserialization
    - Header injection via interceptors (tenant ID, JWT token)
    - Multipart support for file uploads
5. **Infrastructure Integration**: `koki-platform` provides tenant context and authentication token access
6. **Server Communication**: HTTP requests sent to `koki-server` REST endpoints with proper headers
7. **Response Handling**: Typed responses deserialized into DTOs and returned to application code

**Benefits:**

- **Separation of Concerns**: SDK handles HTTP communication, apps focus on business logic
- **Type Safety**: Compile-time verification of request/response structures
- **Consistency**: Shared DTOs ensure client-server contract alignment
- **Maintainability**: Centralized HTTP logic, easier to update when APIs evolve
- **Testability**: Easy to mock SDK clients for unit testing

## API Reference

The koki-sdk provides client classes for all major domains in the Koki platform. Each client wraps REST endpoints with
type-safe methods.

### Available SDK Clients

| Client                 | Description              | Key Methods                                                                                                                                        |
|------------------------|--------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| **KokiAuthentication** | Authentication and login | `login(LoginRequest)`                                                                                                                              |
| **KokiAccounts**       | Account CRUD and search  | `create()`, `update()`, `delete()`, `account()`, `accounts()`, `attributes()`, `uploadAttributes()`                                                |
| **KokiAgent**          | Agent management         | `agent()`, `agents()`, `createAgent()`, `updateAgent()`, `deleteAgent()`                                                                           |
| **KokiConfiguration**  | Tenant configuration     | `configurations()`, `saveConfiguration()`                                                                                                          |
| **KokiContacts**       | Contact management       | `create()`, `update()`, `delete()`, `contact()`, `contacts()`                                                                                      |
| **KokiFiles**          | File upload/download     | `upload()`, `file()`, `files()`, `delete()`, `uploadUrl()`                                                                                         |
| **KokiInvitations**    | User invitations         | `create()`, `invitation()`, `invitations()`, `delete()`                                                                                            |
| **KokiLead**           | Lead tracking            | `create()`, `update()`, `delete()`, `lead()`, `leads()`                                                                                            |
| **KokiListings**       | Property listings        | `create()`, `update()`, `updateAmenities()`, `updateAddress()`, `updateGeoLocation()`, `listing()`, `listings()`, `close()`                        |
| **KokiMessages**       | Messaging system         | `create()`, `message()`, `messages()`                                                                                                              |
| **KokiModules**        | Module management        | `modules()`, `permissions()`                                                                                                                       |
| **KokiNotes**          | Note management          | `create()`, `update()`, `delete()`, `note()`, `notes()`                                                                                            |
| **KokiOffer**          | Offer management         | `create()`, `update()`, `delete()`, `offer()`, `offers()`                                                                                          |
| **KokiOfferVersion**   | Offer versioning         | `create()`, `offerVersion()`, `offerVersions()`                                                                                                    |
| **KokiRefData**        | Reference data           | `amenities()`, `categories()`, `locations()`                                                                                                       |
| **KokiRoles**          | Role management          | `create()`, `update()`, `delete()`, `role()`, `roles()`, `uploadRoles()`                                                                           |
| **KokiTenants**        | Tenant management        | `tenant()`, `tenants()`, `initTenant()`                                                                                                            |
| **KokiTypes**          | Type management          | `create()`, `update()`, `delete()`, `type()`, `types()`, `uploadTypes()`                                                                           |
| **KokiUsers**          | User management          | `create()`, `update()`, `delete()`, `user()`, `users()`, `updateProfile()`, `updatePhoto()`, `sendUsername()`, `sendPassword()`, `resetPassword()` |

### Example Usage

#### Authentication

```kotlin
val response = kokiAuth.login(
    LoginRequest(username = "user@example.com", password = "secret")
)
val accessToken = response.accessToken
```

#### Account Management

```kotlin
// Create account
val createResponse = kokiAccounts.create(
    CreateAccountRequest(
        name = "Acme Corp",
        accountTypeId = 1,
        managedById = 100
    )
)

// Search accounts
val searchResponse = kokiAccounts.accounts(
    keyword = "Acme",
    ids = emptyList(),
    accountTypeIds = listOf(1, 2),
    managedByIds = emptyList(),
    createdByIds = emptyList(),
    limit = 20,
    offset = 0
)

// Get specific account
val account = kokiAccounts.account(id = createResponse.id)
```

#### Listing Management

```kotlin
// Create listing
val listing = kokiListings.create(
    CreateListingRequest(
        title = "Beautiful 3BR Apartment",
        propertyType = PropertyType.APARTMENT,
        listingType = ListingType.SALE,
        price = 500000.0,
        categoryId = 10,
        locationId = 50
    )
)

// Update listing amenities
kokiListings.updateAmenities(
    id = listing.id,
    request = UpdateListingAmenitiesRequest(
        amenityIds = listOf(1, 2, 3)
    )
)
```

#### File Upload

```kotlin
// Upload a file
val uploadResponse = kokiFiles.upload(
    ownerId = accountId,
    ownerType = ObjectType.ACCOUNT,
    type = FileType.DOCUMENT,
    file = multipartFile
)

// Get file metadata
val fileMetadata = kokiFiles.file(id = uploadResponse.id)

// Search files
val files = kokiFiles.files(
    ids = emptyList(),
    ownerId = accountId,
    ownerType = ObjectType.ACCOUNT,
    type = FileType.DOCUMENT,
    status = FileStatus.ACTIVE,
    limit = 20,
    offset = 0
)
```

### Common Patterns

#### Pagination

All search methods support pagination with `limit` and `offset` parameters:

```kotlin
val page1 = kokiAccounts.accounts(
    keyword = null,
    ids = emptyList(),
    accountTypeIds = emptyList(),
    managedByIds = emptyList(),
    createdByIds = emptyList(),
    limit = 20,
    offset = 0  // First page
)

val page2 = kokiAccounts.accounts(
    keyword = null,
    ids = emptyList(),
    accountTypeIds = emptyList(),
    managedByIds = emptyList(),
    createdByIds = emptyList(),
    limit = 20,
    offset = 20  // Second page
)
```

#### Filtering with Collections

Pass lists to filter by multiple IDs or values:

```kotlin
val accounts = kokiAccounts.accounts(
    keyword = null,
    ids = listOf(1, 2, 3),  // Filter by specific IDs
    accountTypeIds = listOf(10, 20),  // Filter by multiple types
    managedByIds = emptyList(),
    createdByIds = emptyList(),
    limit = 50,
    offset = 0
)
```

## License

This project is licensed under the MIT License. See the [LICENSE](../../LICENSE.md) file for details.
