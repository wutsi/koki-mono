# koki-portal

A Spring Boot Kotlin web application providing the Koki browser-based portal (UI orchestration, listing management, lead
tracking, user onboarding, messaging, and account administration).

[![koki-portal CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml)

[![koki-portal CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml)

![Coverage](../../.github/badges/koki-portal-jococo.svg)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

## About the Project

`koki-portal` is the user-facing operational portal for the Koki platform. It aggregates and presents backend
capabilities (accounts, listings, offers, leads, messaging, tenant setup, translation, and AI-assisted flows) via a
secure web interface. The portal consumes Koki APIs through `koki-sdk`, applies server-side rendering (Thymeleaf) for
dynamic views, and enforces JWT authentication and multi-tenancy at the edge.

### Features

- **Integrated Domain UI** – Unified interface for accounts, contacts, listings, offers, notes, leads, and messaging.
- **Secure Access & Roles** – JWT authentication, role-based conditional rendering, tenant isolation with header
  propagation.
- **Server-Side Rendering** – Thymeleaf templates with dynamic fragments, localization, and SEO-friendly markup.
- **Action & Workflow Orchestration** – Guided onboarding, signup, forgot password, share flows, and listing publishing.
- **Extensible Service Adapters** – Uses `koki-sdk` and `koki-platform` for API calls, translation, AI prompts, and file
  handling.

## Getting Started

Run the portal locally to interact with a running `koki-server` backend.

### Prerequisites

- **Java 17+**
- **Maven 3.6+**
- A running instance of `koki-server` (local or remote)
- Valid JWT tokens for protected routes (login flow enabled)

### 1. Clone Repository

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-portal
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

Generates the portal JAR and runs unit/integration tests.

### 4. Run

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/koki-portal-VERSION_NUMBER.jar
```

Portal default URL: `http://localhost:8080`

### 5. Authentication

Login flow issues a JWT. After login, the portal sends `Authorization: Bearer <token>` and `X-Tenant-ID` automatically
for server-side requests.

### 6. Common Environment Overrides

| Purpose              | Property                       | Example                 |
|----------------------|--------------------------------|-------------------------|
| Change port          | `server.port`                  | `9091`                  |
| Custom API URL       | `koki.api.base-url`            | `https://api.koki.prod` |
| Tenant ID            | `koki.tenant.id`               | `42`                    |
| Enable debug logging | `logging.level.com.wutsi.koki` | `DEBUG`                 |

### 7. Health Check

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

### 8. Updating Dependencies

Use placeholder version numbers in a consuming project:

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-portal</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

## License

See the root [License](../../LICENSE.md).

