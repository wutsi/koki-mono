# koki-dto

An immutable Kotlin data contract library centralizing request, response, event, error, security, and reference data
models for the Koki platform.

[![koki-dto CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml)

[![koki-dto CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

## About the Project

**koki-dto** is the single source of truth for all structured data exchanged across Koki services. It defines immutable
Kotlin data classes for REST requests/responses, domain events used in asynchronous messaging, standardized error
envelopes, security/authentication models (JWT principal/decoder), multi-tenant identifiers, and shared reference data (
countries, cities, configuration forms, attribute types). By consolidating these contracts:

- **Consistency** is enforced: every service serializes/deserializes the same shapes
- **Safety** improves: refactors are type-safe and schema drift is eliminated
- **Validation** is standardized: inbound request DTOs carry Jakarta Validation annotations for early rejection of
  malformed inputs
- **Interoperability** rises: event DTOs enable loosely coupled messaging without runtime dependency sharing
- **Security alignment**: shared authentication/identity models prevent divergent token handling patterns

This module purposefully excludes business logic, persistence operations, external network calls, or service
orchestration. It is a pure compile-time library consumed by services such as `koki-server`, `koki-platform`, portals,
chatbots, and SDKs.

## Contributing

We welcome improvements to data models and validation rules. Please review our contribution guidelines.

- See **[CONTRIBUTING.md](../../CONTRIBUTING.md)** for the end-to-end contribution workflow.

### Local Development

Set up your environment, build, and iterate following **[DEVELOP.md](DEVELOP.md)** (module-specific) and the root *
*[DEVELOP.md](../../DEVELOP.md)** for monorepo-wide practices.

### Testing

Validation, serialization, and event model testing guidance is documented in **[TESTING.md](TESTING.md)** (
module-specific) and root **[TESTING.md](../../TESTING.md)**.

## License

See the root [License](../../LICENSE.md).
