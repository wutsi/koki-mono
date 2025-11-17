# koki-dto

Shared data transfer object (DTO) library providing type-safe, validated request/response contracts for all Koki
platform APIs, ensuring consistency between client and server communication.

[![koki-dto-master](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml)
[![koki-dto-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml)

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [High-Level Architecture](#high-level-architecture)
    - [Repository Structure](#repository-structure)
    - [Domain Reference](#domain-reference)
- [Usage Examples](#usage-examples)
- [License](#license)

## Features

- **Comprehensive Domain Coverage**: 195+ DTOs across 16 business domains (accounts, listings, offers, leads, tenants,
  etc.)
- **Type-Safe Contracts**: Kotlin data classes with immutable defaults for safe API communication
- **Jakarta Validation**: Built-in validation annotations for request data integrity
- **Enumerated Types**: 20+ enums for standardized status codes, types, and categories
- **Request/Response Pairs**: Consistent naming conventions (CreateXRequest/CreateXResponse, GetXResponse,
  SearchXResponse)
- **Security Integration**: JWT token encoding/decoding utilities with Auth0 JWT library
- **Error Handling**: Standardized error and parameter DTOs with trace ID support
- **Summary Objects**: Lightweight summary DTOs for efficient list/search operations
- **Zero Dependencies**: Minimal external dependencies (jakarta.validation, java-jwt)
- **Multi-Tenant Ready**: Tenant configuration and user management DTOs
- **Real Estate Focus**: Rich property listing DTOs with addresses, geo-location, and pricing
- **Contact Management**: Contact and account DTOs with custom attributes support
- **File Management**: File upload/download and metadata DTOs
- **Lead Tracking**: Lead generation and status tracking DTOs
- **Messaging Support**: Message and note DTOs for internal communication
- **Offer Management**: Offer lifecycle management from creation to acceptance/rejection

## Technologies

### Programming Languages

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple?logo=kotlin)
![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)

### Tools & Libraries

![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.1.0-orange)
![Auth0 JWT](https://img.shields.io/badge/Auth0%20JWT-4.4.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?logo=apachemaven)

## High-Level Architecture

### Repository Structure

```
koki-dto/
├── src/main/kotlin/com/wutsi/koki/
│   ├── account/dto/          # Account management DTOs (12 files)
│   ├── agent/dto/            # AI agent DTOs (7 files)
│   ├── common/dto/           # Shared DTOs and enums (5 files)
│   ├── contact/dto/          # Contact management DTOs (9 files)
│   ├── error/dto/            # Error handling DTOs (5 files)
│   ├── file/dto/             # File management DTOs (10 files)
│   ├── lead/dto/             # Lead tracking DTOs (11 files)
│   ├── listing/dto/          # Property listing DTOs (25 files)
│   ├── message/dto/          # Messaging DTOs (9 files)
│   ├── module/dto/           # Module management DTOs (5 files)
│   ├── note/dto/             # Note management DTOs (11 files)
│   ├── offer/dto/            # Offer management DTOs (18 files)
│   ├── refdata/dto/          # Reference data DTOs (15 files)
│   ├── security/dto/         # Security and JWT DTOs (5 files)
│   ├── tenant/dto/           # Multi-tenant DTOs (43 files)
│   └── track/dto/            # Analytics tracking DTOs (5 files)
└── pom.xml
```

### Domain Reference

#### Core Business Domains

- **Account** (`account/dto`): Customer and organization account management
    - Account entity with custom attributes
    - Account type definitions
    - Account search and CRUD operations

- **Listing** (`listing/dto`): Real estate property listings (25 DTOs)
    - Comprehensive property details (bedrooms, bathrooms, area, year, etc.)
    - Property types (house, apartment, land, commercial, etc.)
    - Listing types (sale, rent, lease)
    - Amenities, parking, basement, furniture types
    - Address and geo-location integration
    - Pricing and commission management

- **Offer** (`offer/dto`): Property offer lifecycle management (18 DTOs)
    - Offer creation, acceptance, rejection
    - Price negotiation and counter-offers
    - Offer status tracking
    - Buyer and seller agent assignment

- **Lead** (`lead/dto`): Lead generation and tracking (11 DTOs)
    - Lead source tracking (web, referral, social media)
    - Lead status management (new, contacted, qualified, converted, lost)
    - Lead assignment and notes

- **Contact** (`contact/dto`): Contact information management (9 DTOs)
    - Personal contact details
    - Gender and communication preferences
    - Contact search and CRUD operations

#### Infrastructure Domains

- **Tenant** (`tenant/dto`): Multi-tenant platform management (43 DTOs)
    - Tenant configuration (locale, currency, formats, branding)
    - User management (CRUD, search, authentication)
    - Role-based access control (RBAC)
    - Invitation management
    - Configuration key-value pairs
    - Type system management

- **Security** (`security/dto`): Authentication and authorization (5 DTOs)
    - JWT token encoding/decoding (`JWTDecoder`, `JWTEncoder`)
    - JWT principal with claims
    - Login request/response

- **File** (`file/dto`): File storage and management (10 DTOs)
    - File upload with multipart support
    - File metadata (name, URL, content type, size)
    - File type enumeration
    - File search and CRUD operations

- **Error** (`error/dto`): Standardized error responses (5 DTOs)
    - Error entity with code, message, trace ID
    - Parameter errors with type information
    - Downstream error tracking

#### Supporting Domains

- **Message** (`message/dto`): Internal messaging (9 DTOs)
    - Message creation and search
    - Recipient management
    - Message status tracking

- **Note** (`note/dto`): Note and comment management (11 DTOs)
    - Note CRUD operations
    - Note search with filtering
    - Timestamp tracking

- **Agent** (`agent/dto`): AI agent integration (7 DTOs)
    - Agent configuration and management
    - Conversation tracking
    - Agent type definitions

- **Module** (`module/dto`): Platform module management (5 DTOs)
    - Module activation/deactivation
    - Module configuration
    - Module search

- **Track** (`track/dto`): Analytics and tracking (5 DTOs)
    - Event tracking (page views, clicks, conversions)
    - Device and channel type tracking
    - Push tracking requests

- **Refdata** (`refdata/dto`): Reference data and utilities (15 DTOs)
    - Address (street, city, zip, country)
    - GeoLocation (latitude, longitude)
    - Money (amount, currency)
    - Country, city, language definitions

- **Common** (`common/dto`): Shared cross-cutting DTOs (5 DTOs)
    - ObjectType enum (account, contact, file, note, etc.)
    - Common request/response patterns

## Usage Examples

### Creating Request/Response DTOs

```kotlin
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money

// Create a listing request
val request = CreateListingRequest(
    listingType = ListingType.SALE,
    propertyType = PropertyType.HOUSE,
    bedrooms = 3,
    bathrooms = 2,
    propertyArea = 150,
    price = Money(amount = 250000.0, currency = "USD"),
    address = Address(
        street = "123 Main St",
        city = "Springfield",
        zipCode = "12345",
        country = "US"
    )
)
```

### Using Security DTOs

```kotlin
import com.wutsi.koki.security.dto.JWTDecoder
import com.wutsi.koki.security.dto.LoginRequest

// Decode a JWT token
val decoder = JWTDecoder()
val principal = decoder.decode(accessToken)
val userId = principal.id
val tenantId = principal.tenantId

// Create login request
val loginRequest = LoginRequest(
    username = "user@example.com",
    password = "securePassword123"
)
```

### Working with Search Responses

```kotlin
import com.wutsi.koki.listing.dto.SearchListingRequest
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingSort

// Search for active listings
val searchRequest = SearchListingRequest(
    status = ListingStatus.ACTIVE,
    listingTypeIds = listOf(1L, 2L),
    propertyTypeIds = listOf(3L),
    minPrice = 100000.0,
    maxPrice = 500000.0,
    minBedrooms = 2,
    sortBy = ListingSort.PRICE_ASC,
    limit = 20,
    offset = 0
)
```

### Handling Errors

```kotlin
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.error.dto.Parameter
import com.wutsi.koki.error.dto.ParameterType

// Create error response
val error = Error(
    code = "VALIDATION_ERROR",
    message = "Invalid input data",
    parameter = Parameter(
        name = "email",
        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
        value = "invalid-email"
    ),
    traceId = "abc-123-def"
)

val errorResponse = ErrorResponse(error = error)
```

### Multi-Tenant Operations

```kotlin
import com.wutsi.koki.tenant.dto.CreateUserRequest
import com.wutsi.koki.tenant.dto.User
import com.wutsi.koki.tenant.dto.Tenant
import com.wutsi.koki.tenant.dto.TenantStatus

// Create tenant
val tenant = Tenant(
    id = 1L,
    name = "Acme Real Estate",
    domainName = "acme.koki.com",
    locale = "en_US",
    currency = "USD",
    status = TenantStatus.ACTIVE,
    country = "US"
)

// Create user request
val createUserRequest = CreateUserRequest(
    email = "agent@acme.com",
    displayName = "John Smith",
    roleIds = listOf(1L, 2L)
)
```

### Working with Offers

```kotlin
import com.wutsi.koki.offer.dto.CreateOfferRequest
import com.wutsi.koki.offer.dto.AcceptOfferRequest

// Create offer
val offerRequest = CreateOfferRequest(
    listingId = 123L,
    amount = 240000.0,
    notes = "Cash offer, flexible closing date"
)

// Accept offer
val acceptRequest = AcceptOfferRequest(
    offerId = 456L,
    notes = "Accepted - closing in 30 days"
)
```

## License

This project is licensed under the MIT License. See the [LICENSE.md](../../LICENSE.md) file for details.

