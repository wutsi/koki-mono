# koki-dto

An immutable Kotlin data contract library centralizing request, response, event, error, security, and reference data
models for the Koki platform.

![master](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)

![pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)

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

The **koki-dto** module is the single source of truth for all structured data exchanged across Koki services. It defines
immutable Kotlin data classes for REST requests/responses, domain events for asynchronous messaging, standardized error
envelopes, authentication models (login, JWT principal/decoder), multi-tenant identifiers, and shared reference data (
countries, cities, configuration forms, attribute types). By consolidating data contracts here:

- **Consistency**: All services serialize/deserialize identical shapes.
- **Safety**: Strong typing eliminates schema drift and improves refactor confidence.
- **Validation**: Request DTOs carry Jakarta Validation annotations enabling early rejection of malformed input.
- **Interoperability**: Event DTOs enable loosely coupled asynchronous workflows without runtime sharing of
  implementation code.
- **Security Alignment**: Common identity/auth models prevent divergent token handling across services.

Problem it solves: Prevents duplication and divergence of data shapes across independently deployed backend services,
portals, tracking components, chatbots, and SDK clients—reducing maintenance overhead and integration bugs.

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- (For consumers) **Spring Boot 3.5.7+**

### Installation

Clone the repository and build the module:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-dto
mvn clean install
```

Add the dependency in your consuming service's **pom.xml**:

```xml
<dependency>
  <groupId>com.wutsi.koki</groupId>
  <artifactId>koki-dto</artifactId>
  <version>VERSION_NUMBER</version>
</dependency>
```

### Running the Project

This is a library module and is not executed standalone. Include it as a dependency; import its
packages (`com.wutsi.koki.*.dto`).

### Running Tests

Execute tests for this module:

```bash
cd modules/koki-dto
mvn test
```

Generate coverage (if configured):

```bash
mvn clean test jacoco:report
```

Report location:

```
modules/koki-dto/target/site/jacoco/index.html
```

## License

This project is licensed under the MIT License. See **[LICENSE.md](../../LICENSE.md)** for details.
