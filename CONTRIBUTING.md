# Contributing to KOKI

First off, thank you for considering contributing to Koki! It's people like you that make Koki such a great platform.

This document provides guidelines for setting up your local development environment to contribute to the project.

## Table of Contents

* [Prerequisites](#prerequisites)
* [Getting the Code](#getting-the-code)
* [Configuration](#configuration)
* [Building the Application](#building-the-application)
* [Running the Application](#running-the-application)
* [Submitting Changes](#submitting-changes)

------------------------------------------------------------

## Prerequisites

Before you begin, ensure you have the following installed on your system:

* **Git:** For version control. [Download Git](https://git-scm.com/downloads)
* **JDK:** The platform is build on **Java v17**. You can
  install [Oracle JDK v17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  or [OpenJDK](https://jdk.java.net/17/)
* **Maven:** Java Package Manager. [Install Maven v3.9+](https://maven.apache.org/install.html)
* **MySQL:** Database used for persisting the platform
  state. [Install MySQL v8+](https://maven.apache.org/install.html)
    * IMPORTANT: When installing, make sure that `root` user has no password on your local environment.
* **RabbitMQ:** Messaging queue service. [Install RabbitMQ v4+](https://www.rabbitmq.com/docs/download)

## Getting the Code

1. **Fork the repository:** Click the "Fork" button on the top right of the ``koki-mono`` repository page on
   GitHub [https://github.com/wutsi/koki-mono](https://github.com/wutsi/koki-mono). This
   creates a copy of the repository under your GitHub account.

3. **Navigate into the directory:**
   ```bash
   cd koki-mono
   ```

------------------------------------------------------------

## Configuration

Setup the following environment variable need for building and running the application locally:

- **GEMINI_API_KEY**: API Key of [Google Gemini](https://gemini.google.com). You can obtain you API
  key [here](https://aistudio.google.com/app/apikey)

- **STRIPE_API_KEY**: API Key of [Stripe](https://stripe.com/). You can obtain your API
  key [here](https://dashboard.stripe.com/test/apikeys)

------------------------------------------------------------

## Building the Application

To build the whole application:

```bash
mvn install -Dheadless=true
```

------------------------------------------------------------

## Running the Application

### Run the server

```bash
cd modules/koki-server
mvn spring-boot:run
```

### Initialize data

You should this only once, the first time your install the application.

```bash
curl http://localhost:8080/v1/refdata/load
curl http://localhhost:8080/v1/tenants/1/init
````

### Run the portal

```bash
cd ../../modules/koki-portal
mvn spring-boot:run
```

Navigate to [https://localhost:8081](https://localhost:8081) and login with the following information:

- Email: ``admin@gmail.com``
- Password: ``secret``

------------------------------------------------------------

## Submitting Changes

TODO
