# koki-platform

A shared infrastructure library providing cross-cutting concerns for Koki services (storage, messaging, caching,
security, templating, translation, logging, tracing, AI integration).

[![koki-platform CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml)

[![koki-platform CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml)

![Coverage](../../.github/badges/koki-platform-jacoco.svg)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

## About the Project

**koki-platform** centralizes reusable, production-ready infrastructure abstractions so individual Koki microservices
can stay focused on domain logic. Instead of every service re‑implementing storage access, messaging patterns, caching,
multi‑tenant context handling, template rendering, translation, and structured observability, this module offers
consistent, pluggable interfaces backed by proven implementations.

Core value propositions:

- **Consistency** – Unified approach to cross-cutting concerns across all services
- **Maintainability** – Fix or enhance once; every consumer benefits
- **Configurability** – Switch providers (e.g., local vs S3 storage) via configuration without code changes
- **Observability** – Built-in structured logging (KVLogger) and tracing context propagation
- **Multi-tenancy Support** – Tenant-aware storage paths, cache keys, message headers
- **Extensibility** – Factory patterns (e.g., AI provider) allow new integrations with minimal effort

Problems solved:

- Reduces duplicated boilerplate and divergent patterns
- Accelerates new service onboarding by providing ready infrastructure primitives
- Improves reliability with centralized error handling and retry strategies (messaging)
- Standardizes how tenant and security context propagate through infrastructure layers

## Contributing

We welcome contributions! Please review our guidelines before opening issues or pull requests.

- See the full **Contributing Guide**: [CONTRIBUTING.md](../../CONTRIBUTING.md)

### Local Development

Setup instructions and environment details are documented in [DEVELOP.md](../../DEVELOP.md).

### Testing

Testing strategy, tools, and execution instructions are in [TESTING.md](../../TESTING.md).

## License

See the root [License](../../LICENSE.md).
