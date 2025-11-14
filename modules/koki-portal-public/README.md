# koki-portal-public

A Spring Boot Kotlin web application providing the public-facing portal for browsing property listings, viewing details,
and submitting inquiries without authentication.

[![koki-portal-public CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml)

[![koki-portal-public CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml)

![Coverage](../../.github/badges/koki-portal-public-jococo.svg)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

## About the Project

`koki-portal-public` is the consumer-facing web portal for the Koki platform. It provides unauthenticated access to
property listings, search functionality, detailed listing views with image galleries, and inquiry submission forms. The
portal consumes public APIs from `koki-server` via `koki-sdk`, applies server-side rendering with Thymeleaf, and tracks
visitor interactions for analytics.

### Features

- **Public Listing Browsing** – Search and filter property listings without login (price range, location, property type,
  amenities).
- **Detailed Listing Pages** – Rich property detail views with image galleries, maps, amenities, and pricing
  information.
- **Inquiry Submission** – Contact forms for prospective buyers/renters to express interest and request information.
- **SEO-Optimized Markup** – Server-side rendering with semantic HTML and structured data for search engine visibility.
- **Responsive UI** – Mobile-first design with Thymeleaf templates ensuring cross-device compatibility.

## Getting Started

Run the public portal locally to interact with a running `koki-server` backend.

### Prerequisites

- **Java 17+**
- **Maven 3.6+**
- A running instance of `koki-server` (local or remote) with public endpoints enabled

### 1. Clone Repository

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-portal-public
```

### 2. Configure Backend URL & Tenant

Set environment variables or use an `application-local.yml` profile:

```yaml
koki:
    api:
        base-url: http://localhost:8080
    tenant:
        id: 1
```

Activate with:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

### 3. Build

```bash
mvn clean install
```

Generates the portal JAR and runs unit/integration tests (86%+ coverage target).

### 4. Run

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/koki-portal-public-VERSION_NUMBER.jar
```

Portal default URL: `http://localhost:8080`

### 5. Browse Listings

Navigate to:

```
http://localhost:8080/
```

Search and filter listings by:

- Location (city, region)
- Price range
- Property type (house, apartment, villa, etc.)
- Number of bedrooms/bathrooms
- Amenities (pool, parking, etc.)

### 6. View Listing Details

Click any listing to see:

- Full property description
- Image gallery
- Amenities list
- Location map
- Contact/inquiry form

### 7. Submit Inquiry

Fill out the inquiry form with:

- Name
- Email
- Phone (optional)
- Message

No authentication required for submission.

### 8. Common Environment Overrides

| Purpose              | Property                       | Example                 |
|----------------------|--------------------------------|-------------------------|
| Change port          | `server.port`                  | `9092`                  |
| Custom API URL       | `koki.api.base-url`            | `https://api.koki.prod` |
| Tenant ID            | `koki.tenant.id`               | `42`                    |
| Enable debug logging | `logging.level.com.wutsi.koki` | `DEBUG`                 |

### 9. Health Check

If actuator enabled:

```bash
curl http://localhost:8080/actuator/health
```

Expected:

```json
{
    "status": "UP"
}
```

### 10. Updating Dependencies

Use placeholder version numbers in a consuming project:

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-portal-public</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

## License

See the root [License](../../LICENSE.md).

