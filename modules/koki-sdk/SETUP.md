# Setup Guide - koki-sdk

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)

## Prerequisites

Before using the koki-sdk, ensure you have the following installed and configured:

- **Java Development Kit (JDK) 17** or higher
    - Verify: `java -version`
    - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
      or [OpenJDK](https://openjdk.org/)

- **Maven 3.8+** or **Gradle 7+** for dependency management
    - Maven: `mvn -version`
    - Gradle: `gradle -version`

- **Access to koki-server**: A running instance of koki-server (local or remote)
    - Local development: `http://localhost:8080`
    - Test environment: configured endpoint URL

- **GitHub Personal Access Token**: Required to access GitHub Packages where koki-sdk is published
    - Must have `read:packages` scope
    - Create at: https://github.com/settings/tokens

### Optional Tools

- **Spring Boot** (recommended): The SDK is designed to work seamlessly with Spring Boot applications
- **IntelliJ IDEA** or other Kotlin-compatible IDE for development

## Installation

### Maven Installation

Add the koki-sdk dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-sdk</artifactId>
    <version>0.0.51-SNAPSHOT</version>
</dependency>
```

**Important**: The SDK has `provided` scope dependencies that must be included in your project:

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>6.2.3</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot</artifactId>
    <version>3.5.7</version>
</dependency>
```

### Gradle Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.wutsi.koki:koki-sdk:0.0.51-SNAPSHOT")
    implementation("org.springframework:spring-web:6.2.3")
    implementation("org.springframework.boot:spring-boot:3.5.7")
}
```

### GitHub Packages Authentication

Since koki-sdk is published to GitHub Packages, configure authentication in your Maven `settings.xml` or Gradle
configuration:

**Maven (`~/.m2/settings.xml`):**

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
                    <id>koki</id>
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

**Gradle (`~/.gradle/gradle.properties`):**

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.token=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
```

Then in your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/wutsi/koki-mono")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("TOKEN")
        }
    }
}
```

## Configuration

### Basic Spring Boot Configuration

Create a configuration class to initialize SDK clients:

```kotlin
import com.wutsi.koki.sdk.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class KokiSdkConfiguration {

    @Bean
    fun urlBuilder(): URLBuilder {
        return URLBuilder(baseUrl = "http://localhost:8080") // koki-server URL
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun kokiAuthentication(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiAuthentication {
        return KokiAuthentication(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiAccounts(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiAccounts {
        return KokiAccounts(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiListings(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiListings {
        return KokiListings(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiFiles(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiFiles {
        return KokiFiles(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiLeads(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiLead {
        return KokiLead(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiOffers(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiOffer {
        return KokiOffer(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiTenants(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiTenants {
        return KokiTenants(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiUsers(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiUsers {
        return KokiUsers(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiMessages(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiMessages {
        return KokiMessages(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiContacts(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiContacts {
        return KokiContacts(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiNotes(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiNotes {
        return KokiNotes(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiAgent(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiAgent {
        return KokiAgent(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiRefData(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiRefData {
        return KokiRefData(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiRoles(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiRoles {
        return KokiRoles(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiModules(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiModules {
        return KokiModules(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiConfiguration(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiConfiguration {
        return KokiConfiguration(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiInvitations(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiInvitations {
        return KokiInvitations(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiTypes(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiTypes {
        return KokiTypes(urlBuilder, restTemplate)
    }

    @Bean
    fun kokiOfferVersion(urlBuilder: URLBuilder, restTemplate: RestTemplate): KokiOfferVersion {
        return KokiOfferVersion(urlBuilder, restTemplate)
    }
}
```

### Advanced Configuration with Headers

Configure RestTemplate to automatically inject tenant ID and authentication headers:

```kotlin
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.tenant.TenantProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestTemplate

@Configuration
class KokiSdkConfiguration(
    private val tenantProvider: TenantProvider,
    private val accessTokenHolder: AccessTokenHolder
) {

    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(KokiHeaderInterceptor(tenantProvider, accessTokenHolder))
        return restTemplate
    }
}

class KokiHeaderInterceptor(
    private val tenantProvider: TenantProvider,
    private val accessTokenHolder: AccessTokenHolder
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        // Add Authorization header with JWT token
        accessTokenHolder.get()?.let { token ->
            request.headers[HttpHeaders.AUTHORIZATION] = "Bearer $token"
        }

        // Add Tenant ID header
        request.headers["X-Tenant-ID"] = tenantProvider.id().toString()

        return execution.execute(request, body)
    }
}
```

### Configuration Properties

Use Spring Boot properties for environment-specific configuration:

**application.yml:**

```yaml
koki:
  server:
    url: http://localhost:8080  # koki-server base URL
  tenant:
    id: 1  # Default tenant ID
```

**Configuration class:**

```kotlin
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(KokiProperties::class)
class KokiSdkConfiguration(private val properties: KokiProperties) {

    @Bean
    fun urlBuilder(): URLBuilder {
        return URLBuilder(baseUrl = properties.server.url)
    }
}

@ConfigurationProperties(prefix = "koki")
data class KokiProperties(
    val server: ServerProperties = ServerProperties(),
    val tenant: TenantProperties = TenantProperties()
)

data class ServerProperties(
    var url: String = "http://localhost:8080"
)

data class TenantProperties(
    var id: Long = 1
)
```

## Running the Project

### Using the SDK in Your Application

#### 1. Authentication Example

```kotlin
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.security.dto.LoginRequest
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val kokiAuth: KokiAuthentication
) {
    fun login(username: String, password: String): String {
        val response = kokiAuth.login(
            LoginRequest(
                username = username,
                password = password
            )
        )
        return response.accessToken
    }
}
```

#### 2. Account Management Example

```kotlin
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.account.dto.GetAccountResponse
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val kokiAccounts: KokiAccounts
) {
    fun searchAccounts(keyword: String): List<GetAccountResponse> {
        val response = kokiAccounts.accounts(
            keyword = keyword,
            ids = emptyList(),
            accountTypeIds = emptyList(),
            managedByIds = emptyList(),
            createdByIds = emptyList(),
            limit = 50,
            offset = 0
        )
        return response.accounts
    }

    fun getAccount(accountId: Long): GetAccountResponse {
        return kokiAccounts.account(accountId)
    }
}
```

#### 3. File Upload Example

```kotlin
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.UploadFileResponse
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileService(
    private val kokiFiles: KokiFiles
) {
    fun uploadAccountDocument(
        accountId: Long,
        file: MultipartFile
    ): UploadFileResponse {
        return kokiFiles.upload(
            ownerId = accountId,
            ownerType = ObjectType.ACCOUNT,
            type = FileType.DOCUMENT,
            file = file
        )
    }

    fun downloadFile(fileId: String): ByteArray {
        return kokiFiles.content(fileId)
    }
}
```

#### 4. Listing Management Example

```kotlin
import com.wutsi.koki.sdk.KokiListings
import com.wutsi.koki.listing.dto.*
import org.springframework.stereotype.Service

@Service
class ListingService(
    private val kokiListings: KokiListings
) {
    fun createListing(request: CreateListingRequest): CreateListingResponse {
        return kokiListings.create(request)
    }

    fun publishListing(listingId: String) {
        kokiListings.publish(listingId)
    }

    fun searchListings(
        categoryIds: List<Long> = emptyList(),
        locationIds: List<Long> = emptyList(),
        limit: Int = 20
    ): SearchListingResponse {
        return kokiListings.listings(
            ids = emptyList(),
            listingNumbers = emptyList(),
            status = emptyList(),
            categoryIds = categoryIds,
            locationIds = locationIds,
            types = emptyList(),
            accountIds = emptyList(),
            limit = limit,
            offset = 0
        )
    }
}
```

### Running a Spring Boot Application with SDK

```bash
# Using Maven
mvn spring-boot:run

# Using Gradle
gradle bootRun

# Using packaged JAR
java -jar target/my-app-1.0.0.jar
```

## Running Tests

### Unit Testing SDK Integration

Create unit tests for your services that use the SDK:

```kotlin
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.account.dto.GetAccountResponse
import com.wutsi.koki.account.dto.SearchAccountResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class AccountServiceTest {

    private val kokiAccounts: KokiAccounts = mockk()
    private val accountService = AccountService(kokiAccounts)

    @Test
    fun `should search accounts successfully`() {
        // Given
        val keyword = "John"
        val mockResponse = SearchAccountResponse(
            accounts = listOf(
                GetAccountResponse(id = 1, name = "John Doe"),
                GetAccountResponse(id = 2, name = "John Smith")
            )
        )

        every {
            kokiAccounts.accounts(
                keyword = keyword,
                ids = emptyList(),
                accountTypeIds = emptyList(),
                managedByIds = emptyList(),
                createdByIds = emptyList(),
                limit = 50,
                offset = 0
            )
        } returns mockResponse

        // When
        val result = accountService.searchAccounts(keyword)

        // Then
        assertEquals(2, result.size)
        assertEquals("John Doe", result[0].name)
        verify { kokiAccounts.accounts(any(), any(), any(), any(), any(), any(), any()) }
    }
}
```

### Integration Testing with koki-server

For integration tests, ensure koki-server is running:

```kotlin
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.security.dto.LoginRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("integration-test")
class KokiSdkIntegrationTest {

    @Autowired
    private lateinit var kokiAuth: KokiAuthentication

    @Autowired
    private lateinit var kokiAccounts: KokiAccounts

    @Test
    fun `should authenticate and retrieve accounts`() {
        // Authenticate
        val loginResponse = kokiAuth.login(
            LoginRequest(
                username = "test@example.com",
                password = "test-password"
            )
        )

        assertNotNull(loginResponse.accessToken)

        // Search accounts
        val accounts = kokiAccounts.accounts(
            keyword = "",
            ids = emptyList(),
            accountTypeIds = emptyList(),
            managedByIds = emptyList(),
            createdByIds = emptyList(),
            limit = 10,
            offset = 0
        )

        assertTrue(accounts.accounts.isNotEmpty())
    }
}
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AccountServiceTest

# Run with coverage
mvn test jacoco:report

# Gradle
gradle test

# Run tests with specific profile
mvn test -P integration-test
```

