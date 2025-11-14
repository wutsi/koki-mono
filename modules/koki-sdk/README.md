# koki-sdk

A Kotlin client library providing type-safe wrappers around Koki REST APIs for authentication, accounts, listings,
offers, files, and tenant management.

[![koki-sdk CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml)

[![koki-sdk CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

## About the Project

`koki-sdk` simplifies integration with the Koki platform by providing strongly-typed Kotlin client classes that wrap
REST API calls. Built on Spring's `RestTemplate`, it eliminates manual HTTP request construction and offers
domain-specific clients (`KokiAccounts`, `KokiListings`, `KokiFiles`, etc.) with consistent error handling, query
parameter encoding, and multipart upload support.

### Features

- **Domain-Specific Clients** – Pre-built client classes for each API domain (accounts, listings, offers, files,
  contacts, tenants, etc.) with intuitive method names.
- **Type-Safe DTOs** – All requests and responses use shared DTOs from `koki-dto` for compile-time safety and schema
  consistency.
- **Automatic Query Encoding** – Central `URLBuilder` handles path composition and URL-encodes query parameters
  including collection types.
- **Multipart File Upload** – Built-in support for file uploads via `AbstractKokiModule` with proper content-disposition
  headers.

## Getting Started

Add the SDK dependency and configure a `RestTemplate` with authentication headers to start making API calls.

### Prerequisites

- **Java 17+**
- **Maven 3.6+** or **Gradle 7+**
- **Kotlin 2.1.0+**
- Koki API base URL

### Add Dependency

**Maven:**

```xml

<dependency>
    <groupId>com.wutsi.koki</groupId>
    <artifactId>koki-sdk</artifactId>
    <version>VERSION_NUMBER</version>
</dependency>
```

**Gradle (Kotlin DSL):**

```kotlin
dependencies {
  implementation("com.wutsi.koki:koki-sdk:VERSION_NUMBER")
}
```

### Configure GitHub Packages

**Maven:**

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/wutsi/koki-mono</url>
  </repository>
</repositories>
```

**Gradle:**

```kotlin
repositories {
  maven {
    url = uri("https://maven.pkg.github.com/wutsi/koki-mono")
    credentials {
      username = System.getenv("GITHUB_USER")
      password = System.getenv("GITHUB_PASSWORD")
    }
  }
}
```

### Authenticate to GitHub Packages

Set environment variables:

```bash
export GITHUB_USER=your-github-username
export GITHUB_PASSWORD=your-personal-access-token
```

Update **~/.m2/settings.xml**:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>${env.GITHUB_USER}</username>
      <password>${env.GITHUB_PASSWORD}</password>
    </server>
  </servers>
</settings>
```

### Configure RestTemplate

Create a `RestTemplate` with auth and tenant headers:

```kotlin
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

fun createRestTemplate(accessToken: String, tenantId: Long): RestTemplate {
    val template = RestTemplate()
    template.interceptors.add(ClientHttpRequestInterceptor { req, body, exec ->
        req.headers.add("Authorization", "Bearer $accessToken")
        req.headers.add("X-Tenant-ID", tenantId.toString())
        exec.execute(req, body)
    })
    return template
}
```

### Create SDK Clients

```kotlin
import com.wutsi.koki.sdk.URLBuilder
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.sdk.KokiListings

val baseUrl = "https://api.koki.example"
val urlBuilder = URLBuilder(baseUrl)
val restTemplate = createRestTemplate("YOUR_ACCESS_TOKEN", 1L)

val accountsClient = KokiAccounts(urlBuilder, restTemplate)
val listingsClient = KokiListings(urlBuilder, restTemplate)
```

### Create and Retrieve Resources

```kotlin
import com.wutsi.koki.account.dto.CreateAccountRequest

// Create an account
val response = accountsClient.create(
    CreateAccountRequest(
        name = "ACME Corp",
        email = "info@acme.com",
        accountTypeId = 10L
    )
)

// Fetch the created account
val account = accountsClient.account(response.id)
println("Created account: ${account.account.name}")
```

### Search Resources

```kotlin
// Search listings
val results = listingsClient.listings(
    keyword = "villa",
    ids = emptyList(),
    statuses = emptyList(),
    types = emptyList(),
    propertyTypes = emptyList(),
    furnitureTypes = emptyList(),
    cityIds = emptyList(),
    minPrice = null,
    maxPrice = null,
    sort = null,
    limit = 20,
    offset = 0
)
println("Found ${results.listings.size} listings")
```

### Upload Files

```kotlin
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.security.AccessTokenHolder

// Create file client with providers
val filesClient = KokiFiles(
    urlBuilder,
    restTemplate,
    object : TenantProvider { override fun id() = 1L },
    object : AccessTokenHolder { override fun get() = "YOUR_ACCESS_TOKEN" }
)

// Upload a document
val uploadResponse = filesClient.upload(
    ownerId = null,
    ownerType = null,
    type = FileType.DOCUMENT,
    file = multipartFile
)
println("Uploaded file ID: ${uploadResponse.fileId}")
```

### Handle Errors

```kotlin
import org.springframework.web.client.HttpClientErrorException

try {
    val account = accountsClient.account(999)
} catch (e: HttpClientErrorException.NotFound) {
    println("Account not found")
} catch (e: HttpClientErrorException.Unauthorized) {
    println("Authentication required")
} catch (e: HttpClientErrorException) {
    println("Client error: ${e.statusCode}")
}
```

### Best Practices

- **Reuse RestTemplate** – Create one instance per application for connection pooling
- **Batch requests** – Use list parameters (e.g., `ids`) instead of multiple single calls
- **Paginate results** – Increment `offset` by `limit` until fewer results are returned
- **Cache reference data** – Store locations, categories, and amenities locally to reduce API calls

## License

See the project root [License](../../LICENSE.md).

