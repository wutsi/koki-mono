- Validate code coverage thresholds
- Scan dependencies for known security vulnerabilities
- Report results as PR status checks

**Master Branch Workflow** (`koki-dto-master.yml`):

- Build and execute full test suite
- Publish artifact to GitHub Packages
- Tag releases with semantic version numbers
- Generate release notes from commit history
- Notify downstream services of new versions

### Runtime Infrastructure

The **koki-dto** module has no runtime infrastructure requirements. It is a compile-time dependency with no servers,
databases, or external services.

**No Runtime Requirements:**

- No web server or servlet container
- No database connections or connection pools
- No message queues or event streams
- No caching layers or in-memory data stores
- No external API calls or HTTP clients

## Security Considerations

**Input Validation:**

All request DTOs use Jakarta Validation annotations to enforce constraints at deserialization time. Server-side
validation is triggered automatically via the **@Valid** annotation in Spring controller methods. Validation failures
return standardized **ErrorResponse** with detailed parameter-level error information.

**JWT Security:**

The **JWTDecoder** uses the Auth0 JWT library with signature verification enabled. Token issuer is validated against *
*ISSUER = "Koki"**. The default implementation uses **Algorithm.none()** as a placeholder and must be overridden with *
*HMAC256** or **RSA256** in production deployments.

**Dependency Management:**

The module maintains minimal external dependencies to reduce attack surface. Dependencies are regularly updated via
automated tools. Security audits run automatically in the CI/CD pipeline via GitHub Dependabot alerts.

