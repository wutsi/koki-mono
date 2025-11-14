# koki-mono

A Spring Boot monorepo delivering a multi-tenant real estate platform with REST APIs, SDK, portals, and tracking
services.

[![CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/_master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/_master.yml)

[![CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/_pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/_pr.yml)

![Java 17](https://img.shields.io/badge/Java-17-red.svg)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)

![Spring Boot 3.5.7](https://img.shields.io/badge/SpringBoot-3.5.7-green.svg)

![MySQL](https://img.shields.io/badge/Database-MySQL-blue.svg)

## About the Project

**koki-mono** is a comprehensive monorepo for the Koki real estate platform. It provides backend APIs, client SDKs,
operational portals, and tracking services to support property listing management, lead generation, tenant operations,
messaging, payments, and analytics. Built with Spring Boot 3.5.7 and Kotlin, the platform emphasizes multi-tenancy,
security, and extensibility.

### Features

- **Multi-Module Architecture** – Segregated modules for DTOs, platform utilities, SDK, backend server, portals, and
  tracking services.
- **Type-Safe APIs** – Strongly-typed Kotlin DTOs and REST clients ensuring compile-time safety and schema consistency.
- **Multi-Tenant Support** – JWT authentication with per-request tenant isolation (`X-Tenant-ID` headers) enforced
  across all services.
- **Comprehensive Backend Services** – Account management, listings, offers, leads, messaging, payments (Stripe/PayPal),
  document processing, and AI-assisted workflows.
- **Operational & Public Portals** – Secure admin portal for tenant operations and a public-facing portal for consumer
  property browsing.

## Modules

| Name                                                 | Status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [koki-dto](modules/koki-dto)                         | [![koki-dto CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml) [![koki-dto CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml)                                                                                                                                     |
| [koki-platform](modules/koki-platform)               | [![koki-platform CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml) [![koki-platform CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml) ![Coverage](.github/badges/koki-platform-jococo.svg)                                                  |
| [koki-sdk](modules/koki-sdk)                         | [![koki-sdk CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-master.yml) [![koki-sdk CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-sdk-pr.yml)                                                                                                                                     |
| [koki-server](modules/koki-server)                   | [![koki-server CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml) [![koki-server CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml) ![Coverage](.github/badges/koki-server-jococo.svg)                                                                |
| [koki-portal](modules/koki-portal)                   | [![koki-portal CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml) [![koki-portal CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml) ![Coverage](.github/badges/koki-portal-jococo.svg)                                                                |
| [koki-portal-public](modules/koki-portal-public)     | [![koki-portal-public CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-master.yml) [![koki-portal-public CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-public-pr.yml) ![Coverage](.github/badges/koki-portal-public-jococo.svg)               |
| [koki-tracking-server](modules/koki-tracking-server) | [![koki-tracking-server CI (master)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-master.yml) [![koki-tracking-server CI (PR)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-tracking-server-pr.yml) ![Coverage](.github/badges/koki-tracking-server-jococo.svg) |

## License

This project is licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for details.
