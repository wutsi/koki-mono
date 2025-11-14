# koki-sdk

A Kotlin client library providing type-safe wrappers around Koki REST APIs for authentication, accounts, listings,
offers, files, and tenant management.

[![koki-sdk CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml)

[![koki-sdk CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

## About the Project

**koki-sdk** streamlines integration with the Koki platform by exposing domain‑focused Kotlin client classes that
encapsulate HTTP interactions with Koki REST endpoints. Instead of manually crafting requests, handling path/query
encoding, or juggling multipart uploads and error parsing, developers use concise, type‑safe APIs backed by shared DTOs
from `koki-dto`.

Value proposition:

- **Developer Productivity** – Rapid access to accounts, listings, offers, files, tenants, and more through intuitive
  methods
- **Consistency** – Uniform serialization using shared contracts prevents schema drift
- **Reduced Boilerplate** – Eliminates repetitive URL building, query encoding, header management, and multipart
  handling
- **Error Handling Alignment** – Standardized parsing of error envelopes improves resilience and clarity
- **Multi-Tenant Ready** – Simple propagation of tenant and authentication headers encourages correct isolation
  practices

Problems solved:

- Manual HTTP client code scattered across services or external integrations
- Inconsistent query parameter encoding and pagination patterns
- Reimplementation of file upload logic and authentication headers
- Divergent approaches to domain model evolution and validation

The SDK intentionally avoids: direct persistence, business rule execution, or UI concerns. It focuses exclusively on
clean, reliable client-side interaction with Koki APIs.

## Contributing

Contributions (new domain clients, performance improvements, documentation) are welcome. Please review the guidelines
before submitting changes.

See the **[Contributing Guide](../../CONTRIBUTING.md)** for workflow, branching, and pull request standards.

### Local Development

Environment setup and build instructions are documented in [DEVELOP.md](../../DEVELOP.md).

### Testing

Testing practices and guidance for running the test suite are in [TESTING.md](../../TESTING.md).

## License

See the root [License](../../LICENSE.md).
