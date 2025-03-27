[![master](https://github.com/wutsi/koki-mono/actions/workflows/_master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/_master.yml)
[![pull_request](https://github.com/wutsi/koki-mono/actions/workflows/_pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/_pr.yml)

[![koki-dto](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-master.yml)
[![koki-dto-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-dto-pr.yml)

[![koki-platform](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-master.yml)
[![koki-platform-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-platform-pr.yml)
![Coverage](.github/badges/koki-platform-jococo.svg)

[![koki-server](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-master.yml)
[![koki-server-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-server-pr.yml)
![Coverage](.github/badges/koki-server-jococo.svg)

[![koki-portal](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-master.yml)
[![koki-portal-pr](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml/badge.svg)](https://github.com/wutsi/koki-mono/actions/workflows/koki-portal-pr.yml)
![Coverage](.github/badges/koki-portal-jococo.svg)

# Development Environment

## Setup development environment

### Install MySQL

[Mysql](https://www.mysql.com/) v8.x+ is used by the platform for storing data.

#### Mac OS Platform

```
brew install mysql
```

#### Other platforms

Follow download instruction from [here](https://dev.mysql.com/downloads/installer)

### Install RabbitMQ

[RabbitMQ](https://www.rabbitmq.com/) is used as messaging queue service.

#### Mac OS Platform

You can install with [Homebrew](https://brew.sh/) package manager.

```
brew install rabbitmq
```

#### Other platforms

Follow download instruction from [here](https://www.rabbitmq.com/docs/download)

### Environment Variables

These are the environment variable needed for building the project:

- ``GEMINI_API_KEY``: API Key of [Google Gemini](https://gemini.google.com). You can obtain you API
  key [here](https://aistudio.google.com/app/apikey)
- ``STRIPE_API_KEY``: API Key of [Stripe](https://stripe.com/). You can obtain your API
  key [here](https://dashboard.stripe.com/test/apikeys)
