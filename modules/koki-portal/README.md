# koki-portal

A Spring Boot web application providing server-side rendered administrative interfaces for the Koki platform.

![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml/badge.svg)

![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml/badge.svg)

![JaCoCo](../../.github/badges/koki-portal-jacoco.svg)

![Java](https://img.shields.io/badge/Java-17-blue)

![Kotlin](https://img.shields.io/badge/Kotlin-language-purple)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)

## Table of Contents

- [About the Project](#about-the-project)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Project](#running-the-project)
    - [Running Tests](#running-tests)
- [License](#license)

## About the Project

The **koki-portal** is a server-side rendered web application that serves as the primary administrative interface for
the Koki platform. It provides user-friendly web pages for managing accounts, contacts, listings, leads, messages,
files, offers, and tenant configurations. Built with Spring Boot and Thymeleaf, the portal integrates with the
koki-server backend via the koki-sdk client, implementing session-based authentication with JWT tokens and providing a
responsive, accessible user experience.

Key features include:

- **Server-Side Rendered UI**: Thymeleaf templates for dynamic HTML generation with SEO benefits
- **Comprehensive Management Pages**: Web interfaces for all Koki platform features including accounts, contacts,
  listings, leads, messaging, files, and offers
- **Session-Based Authentication**: Secure login flows with JWT token handling and HTTP-only cookies
- **Form Handling**: Robust form processing with validation, error handling, and user feedback
- **Multi-Tenant Support**: Tenant-aware navigation and data isolation
- **Responsive Design**: Mobile-friendly interfaces using modern CSS frameworks
- **Internationalization**: Multi-language support with message bundles
- **Integration via SDK**: Seamless communication with koki-server REST APIs through the SDK client

## Getting Started

### Prerequisites

Before running the project, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+**
- **Koki Server**: The backend API must be running and accessible

### Installation

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-portal
```

2. Build the project:

```bash
mvn clean install
```

3. Configure the application by creating **application-local.yml** (optional):

```yaml
server:
    port: 8081

koki:
    webapp:
        client-id: koki-portal
    server:
        url: http://localhost:8080

wutsi:
    platform:
        cache:
            type: none
        storage:
            type: local
            local:
                directory: ${user.home}/__wutsi
```

### Running the Project

Run the application locally:

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/koki-portal-VERSION_NUMBER.jar
```

The portal will start on port **8081** by default.

Access the portal:

```
http://localhost:8081
```

Verify the service is running:

```bash
curl http://localhost:8081/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

### Running Tests

Execute unit tests:

```bash
mvn test
```

Run all tests including integration tests:

```bash
mvn verify
```

Generate test coverage report:

```bash
mvn clean test jacoco:report
```

The coverage report will be available at **target/site/jacoco/index.html**.

## License

This project is licensed under the MIT License. See [LICENSE.md](../../LICENSE.md) for details.

