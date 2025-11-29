<!--
Module: koki-portal
Generated: November 21, 2025
Description: Administrative web portal for the Koki multi-tenant real estate platform providing comprehensive property and account management with server-side rendering using Spring Boot and Thymeleaf.
applyTo: modules/koki-portal/**
-->

# Tech Stack

## Languages & Frameworks

- **Kotlin**: Primary programming language with Java 17 compatibility
- **Spring Boot 3.x**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring MVC**: Web framework with server-side rendering
- **Thymeleaf**: Server-side HTML template engine
- **Maven**: Build tool and dependency management

## Frontend Technologies

- **HTML5**: Page markup
- **CSS3**: Styling
- **JavaScript**: Client-side interactions
- **Bootstrap**: Responsive UI framework
- **Thymeleaf fragments**: Reusable UI components

## Backend Integration

- **Koki SDK**: Client library for Koki Server REST API
- **Koki DTO**: Data transfer objects for API communication
- **Koki Platform**: Shared platform utilities and security

## Security & Authentication

- **JWT (JSON Web Tokens)**: Token-based authentication with Auth0 java-jwt library
- **Spring Security**: Session management, CSRF protection, role-based access control
- **Redis**: Session storage and caching

## Testing

- **Selenium WebDriver**: End-to-end UI testing with Chrome driver
- **Spring Boot Test**: Integration testing framework
- **Mockito Kotlin**: Mocking framework for unit tests
- **JUnit/Kotlin Test**: Test assertions and test structure

## Additional Libraries

- **libphonenumber**: Phone number validation and formatting
- **Apache Commons Text**: Text manipulation utilities
- **Apache Commons Net**: Network utilities
- **Jackson**: JSON serialization/deserialization

# Coding Style and Idioms

## Kotlin Conventions

- Use idiomatic Kotlin: data classes, named parameters, default arguments, extension functions
- Prefer immutability: use `val` over `var`, immutable collections
- Use null safety: nullable types (`?`), safe calls (`?.`), Elvis operator (`?:`)
- Leverage Kotlin stdlib: `let`, `apply`, `also`, `run`, `with`
- Use property delegation where appropriate
- Prefer expression bodies for single-expression functions

## Spring Boot Patterns

- Controllers annotated with `@Controller` for web pages
- Services annotated with `@Service` for business logic
- Use constructor-based dependency injection (Kotlin primary constructor)
- Follow MVC pattern: Controller → Service → SDK/API → Mapper → Model
- Use `@ModelAttribute` to provide common model attributes across controllers

## Naming Conventions

- Controllers: `{Entity}Controller`, `List{Entity}Controller`, `Edit{Entity}Controller`, `Create{Entity}Controller`
- Services: `{Entity}Service`
- Mappers: `{Entity}Mapper`
- Models: `{Entity}Model` (for UI presentation models)
- Forms: `{Entity}Form` (for form binding)
- Templates: Follow URL structure (e.g., `/users/show` → `templates/users/show.html`)
- Use descriptive variable names in English
- Use `id` for entity identifiers, `{entity}Id` for foreign keys

## Code Organization

- Package by feature/module (e.g., `listing`, `user`, `contact`, `account`)
- Each module contains: `page/` (controllers), `service/`, `mapper/`, `model/`, `form/`
- Common/shared code in `common/` package
- Configuration in `config/` package
- Security components in `security/` package

## Error Handling

- Catch `HttpClientErrorException` from SDK calls
- Transform exceptions to `ErrorResponse` using `toErrorResponse()`
- Use custom error pages for user-friendly error messages
- Log errors with structured logging using `KVLogger`

# Architecture

## Component Structure

### Presentation Layer (Controllers)

- **Base Controller**: `AbstractPageController` - provides common functionality for all page controllers
    - Access to current user via `userHolder.get()`
    - Access to current tenant via `tenantHolder.get()`
    - Access to feature toggles via `togglesHolder.get()`
    - Common utility methods: `createPageModel()`, `toErrorResponse()`, `getMessage()`
    - Model attributes: `@ModelAttribute` for `user`, `tenant`, `toggles`
- **Page Controllers**: Handle HTTP requests, render Thymeleaf templates
    - Return view names as strings (e.g., `"listings/show"`)
    - Populate `Model` with data for templates
    - Handle form submissions with form binding
    - Use `@RequestMapping` for base paths, `@GetMapping`/`@PostMapping` for specific endpoints
    - Apply `@RequiresPermission` for authorization checks

### Service Layer

- **Business Logic Services**: Encapsulate business rules and orchestrate API calls
    - Call Koki SDK methods to interact with backend API
    - Transform data between DTOs and Models
    - Handle data aggregation from multiple API endpoints
    - Example: `ListingService`, `UserService`, `AccountService`
- **Utility Services**: Provide helper functionality
    - `CurrentUserHolder`: Thread-safe current user access
    - `CurrentTenantHolder`: Thread-safe current tenant access
    - `TogglesHolder`: Feature toggle management
    - `Moment`: Date/time formatting
    - `ObjectReferenceService`: Object reference handling

### Data Transformation Layer (Mappers)

- **Mappers**: Convert between DTOs (from API) and Models (for UI)
    - Annotated with `@Service` for Spring injection
    - Methods: `to{Entity}Model(dto, ...)`
    - Handle data enrichment (e.g., formatting dates, building display text)
    - Aggregate related data from multiple sources
    - Extend `TenantAwareMapper` for tenant-specific formatting

### View Layer (Thymeleaf Templates)

- **Templates**: Server-side HTML generation with Thymeleaf syntax
    - Located in `src/main/resources/templates/`
    - Use layout fragments: `th:replace="~{__components/layout :: head}"`
    - Access model data: `${variable}`, `${object.property}`
    - Conditional rendering: `th:if`, `th:unless`
    - Iteration: `th:each`
    - URL building: `@{/path}`
    - Internationalization: `#{message.key}`
- **Layout Fragments**: Reusable UI components in `templates/__components/`
    - `layout :: head` - HTML head with common meta tags and CSS
    - `layout :: navbar` - Top navigation bar
    - `layout :: menubar` - Side menu navigation
    - Custom components for forms, tables, modals, etc.

### Security Layer

- **SecurityConfiguration**: Spring Security configuration
    - JWT-based authentication with custom filter
    - Session management with Redis
    - URL-based authorization rules
    - Form login with custom login page
    - Logout handler
- **RequiresPermission**: Custom annotation for method-level authorization
    - Applied to controller methods
    - Checks user permissions before allowing access

## Folder Structure

```
modules/koki-portal/
├── src/main/kotlin/com/wutsi/koki/portal/
│   ├── Application.kt                    # Spring Boot entry point
│   ├── account/                          # Account management module
│   │   ├── form/                         # Form beans
│   │   ├── mapper/                       # DTO to Model mappers
│   │   ├── model/                        # UI models
│   │   ├── page/                         # Controllers
│   │   └── service/                      # Business services
│   ├── agent/                            # Agent management
│   ├── ai/                               # AI settings
│   ├── common/                           # Shared utilities
│   │   ├── mapper/                       # Common mappers
│   │   ├── model/                        # Common models (PageModel, etc.)
│   │   ├── page/                         # AbstractPageController
│   │   └── service/                      # Utility services
│   ├── config/                           # Spring configuration
│   ├── contact/                          # Contact management
│   ├── email/                            # Email settings
│   ├── error/                            # Error handling
│   ├── file/                             # File management
│   ├── forgot/                           # Password recovery
│   ├── home/                             # Dashboard
│   ├── lead/                             # Lead management
│   ├── listing/                          # Property listings
│   ├── message/                          # Messaging
│   ├── module/                           # Module/permission management
│   ├── note/                             # Notes
│   ├── offer/                            # Offers
│   ├── refdata/                          # Reference data (categories, locations)
│   ├── security/                         # Authentication/authorization
│   ├── settings/                         # Settings
│   ├── share/                            # Sharing functionality
│   ├── signup/                           # User registration
│   ├── tenant/                           # Tenant management
│   ├── translation/                      # Translation services
│   └── user/                             # User management
├── src/main/resources/
│   ├── application.yml                   # Main configuration
│   ├── application-test.yml              # Test configuration
│   ├── application-prod.yml              # Production configuration
│   ├── messages.properties               # English i18n
│   ├── messages_en.properties            # English i18n
│   ├── templates/                        # Thymeleaf templates
│   │   ├── __components/                 # Reusable components
│   │   │   └── layout/                   # Layout fragments
│   │   ├── accounts/                     # Account views
│   │   ├── agents/                       # Agent views
│   │   ├── contacts/                     # Contact views
│   │   ├── error/                        # Error pages
│   │   ├── home/                         # Home/dashboard
│   │   ├── leads/                        # Lead views
│   │   ├── listings/                     # Listing views
│   │   ├── messages/                     # Message views
│   │   ├── offers/                       # Offer views
│   │   ├── security/                     # Login/logout pages
│   │   ├── settings/                     # Settings pages
│   │   ├── users/                        # User views
│   │   └── ...
│   └── public/                           # Static assets
└── src/test/
    ├── kotlin/com/wutsi/koki/portal/
    │   ├── AbstractPageControllerTest.kt # Base test class
    │   ├── {Module}Fixtures.kt           # Test data fixtures
    │   └── {module}/page/*ControllerTest.kt # Controller tests
    └── resources/
        └── {test-data-files}
```

## Data Flow

1. **HTTP Request** → Spring DispatcherServlet
2. **Security Filter** → JWT validation, session check
3. **Controller Method** → Handle request, extract parameters
4. **Service Layer** → Business logic, call Koki SDK
5. **Koki SDK** → HTTP REST call to Koki Server API
6. **API Response** → DTOs returned from SDK
7. **Mapper** → Transform DTOs to UI Models with enrichment
8. **Controller** → Populate Spring Model with data
9. **Thymeleaf Engine** → Render HTML template with model data
10. **HTTP Response** → Send rendered HTML to browser

## Key Patterns

- **MVC Pattern**: Clear separation of Controller, Model (Service), and View (Thymeleaf)
- **API Gateway Pattern**: Portal acts as gateway to backend Koki Server
- **Repository Pattern**: SDK abstracts API calls (Koki SDK acts as repository)
- **Mapper Pattern**: Separate transformation logic from business logic
- **Template Method Pattern**: AbstractPageController provides template methods
- **Dependency Injection**: Constructor-based DI throughout

# Testing Guidelines

## Test Structure

- **Base Test Class**: `AbstractPageControllerTest`
    - Sets up Selenium WebDriver for UI testing
    - Mocks all SDK dependencies (`RestTemplate`, `KokiSDK`)
    - Provides test fixtures and helper methods
    - Configures test profile: `@ActiveProfiles(profiles = ["qa"])`
    - Random port for test server: `@SpringBootTest(webEnvironment = RANDOM_PORT)`
    - Test context cleanup: `@DirtiesContext(classMode = BEFORE_CLASS)`

## Test Naming

- Test classes: `{ControllerName}Test.kt`
- Test methods: descriptive names using backticks for readability
    - Example: `fun \`show page with valid listing\`()`
    - Example: `fun \`login required\`()`

## Test Patterns

1. **Setup Phase**: Mock API responses
   ```kotlin
   doReturn(GetListingResponse(listing = listing))
       .whenever(rest).getForEntity(any(), eq(GetListingResponse::class.java))
   ```

2. **Action Phase**: Navigate and interact with UI
   ```kotlin
   navigateTo("/listings/${listing.id}")
   click("#btn-edit")
   ```

3. **Assertion Phase**: Verify page state and behavior
   ```kotlin
   assertCurrentPageIs(PageName.LISTING)
   assertElementPresent("#listing-title")
   ```

## Test Coverage Requirements

- **Line Coverage**: Minimum 80% (configured via `jacoco.threshold.line`)
- **Class Coverage**: Minimum 80% (configured via `jacoco.threshold.class`)
- Focus on controller integration tests using Selenium
- Mock external dependencies (SDK, REST APIs)
- Test authentication/authorization scenarios

## Common Test Utilities

- `navigateTo(url)`: Navigate to a page
- `click(selector)`: Click an element
- `input(selector, value)`: Enter text in input
- `assertCurrentPageIs(pageName)`: Verify current page
- `assertElementPresent(selector)`: Verify element exists
- `assertElementNotPresent(selector)`: Verify element doesn't exist
- `setUpAnonymousUser()`: Simulate unauthenticated user
- `getUser()`: Get current test user

## Testing Best Practices

- Use descriptive test names that explain the scenario
- Test happy path and error scenarios
- Test permission checks (authenticated vs anonymous)
- Use fixtures for test data (e.g., `ListingFixtures`, `UserFixtures`)
- Clean up WebDriver after tests
- Use headless Chrome for CI/CD environments (`headless=true` system property)
- Mock all external HTTP calls to ensure test isolation

# Documentation Guidelines

## Code Documentation

- **Classes**: Document purpose, responsibilities, and key dependencies
- **Public Methods**: Document parameters, return values, and business logic
- **Complex Logic**: Add inline comments explaining "why" not "what"
- Use KDoc format for documentation:
  ```kotlin
  /**
   * Retrieves a listing by ID with optional full graph loading.
   * @param id The listing ID
   * @param fullGraph If true, loads all related entities
   * @return The listing model with enriched data
   */
  fun get(id: Long, fullGraph: Boolean = true): ListingModel
  ```

## Configuration Documentation

- Document configuration properties in `application.yml` with comments
- Explain environment-specific configurations
- Document feature toggles and their impact

## Template Documentation

- Add HTML comments to explain complex template logic
- Document Thymeleaf fragments and their parameters
- Explain dynamic behavior and AJAX interactions

## README Updates

- Keep module README.md up-to-date with features
- Document new modules/features as they're added
- Include architecture diagrams for complex features
- Provide setup instructions for new developers

# Behavior

## Controller Behavior

- **Extend AbstractPageController**: All page controllers should extend `AbstractPageController`
- **Use Model Attributes**: Leverage `@ModelAttribute` for common data (user, tenant, toggles)
- **Permission Checks**: Apply `@RequiresPermission` annotation for access control
- **Page Model Creation**: Use `createPageModel()` to create consistent page metadata
- **Error Handling**: Catch and transform exceptions, redirect to error pages
- **Form Validation**: Use Spring validation annotations, display errors in templates
- **Flash Attributes**: Use redirectAttributes for post-redirect-get pattern
- **Return View Names**: Return template paths as strings (e.g., `"listings/show"`)

## Service Behavior

- **Single Responsibility**: Each service handles one domain/module
- **SDK Delegation**: Use Koki SDK for all backend API calls
- **Data Aggregation**: Fetch and combine data from multiple API endpoints
- **Error Propagation**: Let exceptions bubble up to controller layer
- **Full Graph Loading**: Support optional `fullGraph` parameter for related data
- **Pagination Support**: Implement `limit` and `offset` parameters for list operations

## Mapper Behavior

- **One-Way Transformation**: Map DTOs → Models (not bidirectional)
- **Data Enrichment**: Format dates, currencies, build display text
- **Null Safety**: Handle nullable fields gracefully
- **Locale Awareness**: Use `LocaleContextHolder.getLocale()` for formatting
- **Tenant Context**: Use `TenantAwareMapper` for tenant-specific formatting
- **Immutable Models**: Return immutable data classes

## Template Behavior

- **Responsive Design**: Use Bootstrap classes for mobile-first responsive layouts
- **Accessibility**: Use semantic HTML, ARIA labels, keyboard navigation
- **Performance**: Minimize inline CSS/JS, use fragment caching
- **Internationalization**: Use `#{}` for all user-facing text
- **Conditional Rendering**: Use `th:if`/`th:unless` for permission-based UI
- **AJAX Fragments**: Use `data-component-id="ajax-fragment"` for dynamic content loading
- **Form CSRF**: Include CSRF token in all forms (handled by Spring Security)

## Security Behavior

- **JWT Validation**: All authenticated requests must have valid JWT
- **Session Management**: Use Redis for distributed session storage
- **Permission Checking**: Verify user permissions before showing UI or executing actions
- **Secure Defaults**: Deny access by default, explicitly allow in SecurityConfiguration
- **CSRF Protection**: Enable CSRF for all state-changing operations
- **Logout Handling**: Clean up session and redirect to login page

## Error Handling Behavior

- **User-Friendly Messages**: Show clear, actionable error messages
- **Error Pages**: Custom error pages for 403, 404, 500, suspended account
- **Logging**: Log errors with context (user ID, tenant ID, request details)
- **Graceful Degradation**: Show partial data when some API calls fail
- **Validation Errors**: Display field-level errors in forms

## Internationalization Behavior

- **Message Keys**: Use hierarchical message keys (e.g., `page.listing.show.title`)
- **Default Language**: English is the default language
- **Locale Detection**: Use browser locale or user preference
- **Parameterized Messages**: Support message parameters with `getMessage(key, args)`
- **Date/Number Formatting**: Use locale-specific formatting

## Performance Behavior

- **Lazy Loading**: Load related data only when needed (`fullGraph` parameter)
- **Pagination**: Use limit/offset for large result sets
- **Caching**: Leverage Redis for session and frequently accessed data
- **Compression**: Enable GZIP compression for responses (configured in application.yml)
- **Connection Pooling**: Use connection pooling for HTTP clients
- **Async Operations**: Use async operations for non-blocking I/O where appropriate

