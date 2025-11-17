# Setup Guide - koki-dto

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running Tests](#running-tests)

## Prerequisites

The koki-dto is a pure data transfer object (DTO) library with no runtime dependencies. To work with it:

- **Java Development Kit (JDK) 17** or higher
    - Verify: `java -version`
    - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

- **Maven 3.8+** for dependency management and build
    - Verify: `mvn -version`
    - Download: [Apache Maven](https://maven.apache.org/download.cgi)

## Installation

### As a Library Developer

1. Clone the repository:

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono/modules/koki-dto
```

2. Build the library:

```bash
mvn clean install
```

The compiled JAR will be installed to your local Maven repository at:

```
~/.m2/repository/com/wutsi/koki/koki-dto/0.0.281-SNAPSHOT/
```

### As a Consumer Application

Add the dependency to your Maven project:

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-dto</artifactId>
    <version>0.0.281-SNAPSHOT</version>
</dependency>
```

For Gradle projects:

```gradle
implementation 'com.wutsi.koki:koki-dto:0.0.281-SNAPSHOT'
```

### GitHub Packages Authentication

Since koki-dto is published to GitHub Packages, configure authentication in your Maven `settings.xml`:

```xml

<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_GITHUB_USERNAME</username>
            <password>YOUR_GITHUB_PERSONAL_ACCESS_TOKEN</password>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/wutsi/koki-mono</url>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>
</settings>
```

### Dependencies

The library has minimal external dependencies:

- **Jakarta Validation API** - For validation annotations on DTOs
- **Auth0 Java JWT** - For JWT token utilities

These are automatically included when you add koki-dto to your project.

## Configuration

This module requires **no runtime configuration** as it contains only data classes, enums, and utility classes.

### Using DTOs in Your Application

Simply import the DTOs you need:

```kotlin
import com.wutsi.koki.listing.dto.CreateListingRequest
import com.wutsi.koki.listing.dto.CreateListingResponse
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType

// Use the DTOs
val request = CreateListingRequest(
    title = "Beautiful Villa",
    description = "Spacious 4-bedroom villa",
    status = ListingStatus.DRAFT,
    type = ListingType.SALE,
    price = 500000.0
)
```

### Validation

DTOs include Jakarta Validation annotations. To enable validation in your Spring Boot application:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Then use `@Valid` in your controllers:

```kotlin
@RestController
class ListingController {
    @PostMapping("/listings")
    fun createListing(@Valid @RequestBody request: CreateListingRequest): CreateListingResponse {
        // Validation happens automatically
    }
}
```

### JWT Utilities

The module includes JWT encoding/decoding utilities:

```kotlin
import com.wutsi.koki.security.dto.JWTEncoder
import com.wutsi.koki.security.dto.JWTDecoder

// Encode a token
val encoder = JWTEncoder("your-secret-key")
val token = encoder.encode(userId, tenantId, expirySeconds)

// Decode a token
val decoder = JWTDecoder("your-secret-key")
val claims = decoder.decode(token)
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=JWTEncoderTest
```

### Run Tests Matching Pattern

```bash
mvn test -Dtest=*JWT*
```

### Full Build with Verification

```bash
mvn clean verify
```

This command:

1. Compiles the code
2. Runs all tests
3. Packages the JAR
4. Verifies the build

### Skip Tests (Quick Build)

To build without running tests:

```bash
mvn clean package -DskipTests
```

### Install Locally

To install the library to your local Maven repository for use by other modules:

```bash
mvn clean install
```

## Troubleshooting

| Issue                       | Symptom                      | Resolution                                                 |
|-----------------------------|------------------------------|------------------------------------------------------------|
| Build fails                 | Compilation errors           | Ensure JDK 17+ and Maven 3.8+ are installed                |
| Dependency resolution fails | Cannot download dependencies | Configure GitHub Packages authentication in `settings.xml` |
| Validation not working      | No validation errors         | Add `spring-boot-starter-validation` to your project       |
| JWT utilities not found     | Import errors                | Ensure koki-dto dependency is correctly added              |
| Version mismatch            | NoClassDefFoundError         | Check that all Koki modules use compatible versions        |

### Common Issues

#### Missing Jakarta Validation

If you get compilation errors related to validation annotations:

```xml

<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

#### JWT Secret Key Issues

The JWT utilities require a secret key. In production, use a strong secret:

```kotlin
// Don't hardcode secrets in production!
val secret = System.getenv("JWT_SECRET") ?: "default-secret-for-dev"
val encoder = JWTEncoder(secret)
```

### Logging

This module doesn't include logging as it's a pure DTO library. However, you can enable logging in consuming
applications to see validation errors:

```yaml
logging:
    level:
        org.springframework.web.bind: DEBUG
```

## Next Steps

After setting up koki-dto:

1. **Explore DTOs**: Review the README for a complete list of available DTOs
2. **Understand Domain Structure**: Check the domain reference for organization
3. **Use in Your API**: Import DTOs for request/response contracts
4. **Enable Validation**: Add validation starter to your Spring Boot app
5. **Implement Security**: Use JWT utilities for authentication
6. **Stay Synchronized**: Keep koki-dto version aligned with other Koki modules

## Additional Resources

- [README.md](README.md) - Full DTO reference and domain organization
- [koki-server](../koki-server/README.md) - Example server using DTOs
- [koki-sdk](../koki-sdk/README.md) - Client SDK using DTOs
- [Jakarta Validation Documentation](https://beanvalidation.org/)
- [Auth0 JWT Library](https://github.com/auth0/java-jwt)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)

