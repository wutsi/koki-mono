# Setup Guide - koki-server

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)

## Prerequisites

Before running the koki-server, ensure you have the following installed and configured:

- **Java Development Kit (JDK) 17** or higher
    - Verify: `java -version`
    - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
      or [OpenJDK](https://openjdk.org/)

- **Maven 3.8+** for dependency management
    - Verify: `mvn -version`
    - Download from: [Maven](https://maven.apache.org/download.cgi)

- **MySQL 8.x** database server
    - Verify: `mysql --version`
    - Download from: [MySQL](https://dev.mysql.com/downloads/mysql/)

- **RabbitMQ 4.x+** message broker for domain event publishing
    - Verify: `rabbitmqctl status`
    - Download from: [RabbitMQ](https://www.rabbitmq.com/download.html)

### Optional Tools

- **Redis 7.x+** for caching (optional but recommended for production)
    - Verify: `redis-cli ping`
    - Download from: [Redis](https://redis.io/download/)

- **Docker & Docker Compose** for containerized service dependencies
    - Download from: [Docker](https://www.docker.com/get-started)

- **AWS CLI** if using S3 storage backend
    - Download from: [AWS CLI](https://aws.amazon.com/cli/)

- **IntelliJ IDEA** or other Kotlin-compatible IDE for development

### Quick Start With Docker (Optional)

If you prefer to use Docker for dependencies:

```bash
# Start MySQL
docker run -d --name koki-mysql \
  -e MYSQL_ROOT_PASSWORD=secret \
  -e MYSQL_DATABASE=koki \
  -p 3306:3306 \
  mysql:8

# Start RabbitMQ with management UI
docker run -d --name koki-rabbit \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# Start Redis
docker run -d --name koki-redis \
  -p 6379:6379 \
  redis:7-alpine
```

RabbitMQ Management UI: http://localhost:15672 (default credentials: guest/guest)

## Installation

### Clone the Repository

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono
```

### Build Shared Modules

Build the required dependency modules first:

```bash
mvn clean install -DskipTests
```

This will build and install:

- `koki-dto` - Data Transfer Objects
- `koki-platform` - Platform utilities

### Build the Server Module

```bash
cd modules/koki-server
mvn clean package
```

The compiled JAR will be available at: `target/koki-server-1.0.0-SNAPSHOT.jar`

## Database Setup

The koki-server uses MySQL with Flyway for database migrations. Migrations are located in:

```
src/main/resources/db/migration/common/  # Core schema migrations
src/main/resources/db/migration/local/   # Local development seed data
src/main/resources/db/migration/test/    # Test environment data
```

### Create the Database

Connect to MySQL and create the database:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS koki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### Grant Privileges

Ensure the database user has appropriate privileges:

```bash
mysql -u root -p -e "GRANT ALL PRIVILEGES ON koki.* TO 'root'@'localhost';"
mysql -u root -p -e "FLUSH PRIVILEGES;"
```

### Run Migrations

Flyway migrations run automatically when the application starts. The application will:

1. Check for pending migrations
2. Execute them in order (based on version numbers)
3. Record applied migrations in the `flyway_schema_history` table

### Reset Database (Development)

If you need to reset the database during development:

```bash
mysql -u root -p -e "DROP DATABASE IF EXISTS koki;"
mysql -u root -p -e "CREATE DATABASE koki CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Then restart the application to re-run all migrations.

### Seed Data

The `local` profile includes seed scripts that insert:

- Sample tenant configuration
- Default roles and permissions
- Test users
- Reference data

To use seed data, activate the `local` profile when running the application (see Configuration section).

## Configuration

The application uses Spring Boot configuration with profile-specific overrides.

### Base Configuration

Default configuration is in `src/main/resources/application.yml`:

- Server port: `8080`
- Database URL: `jdbc:mysql://localhost:3306/koki`
- Flyway migration locations
- JPA/Hibernate settings
- Swagger UI configuration
- File upload limits (10MB)

### Profile-Specific Configuration

Create profile-specific configuration files for different environments:

#### Local Development (`application-local.yml`)

Create `src/main/resources/application-local.yml`:

```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/koki?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
        username: root
        password: secret
    flyway:
        locations: "classpath:db/migration/common,classpath:db/migration/local"

logging:
    level:
        com.wutsi: DEBUG
        org.springframework: INFO

koki:
    webapp:
        server-url: http://localhost:8080
```

#### Production Configuration

For production, use environment variables or `application-prod.yml`.

### Environment Variables

You can override configuration using environment variables:

```bash
# Database
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/koki?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="secret"

# Server
export SERVER_PORT=8080

# RabbitMQ
export RABBITMQ_URL="amqp://localhost"

# Redis (optional)
export REDIS_URL="redis://localhost:6379"

# Storage Backend
export STORAGE_TYPE=local  # or s3
export AWS_ACCESS_KEY_ID="YOUR_ACCESS_KEY"
export AWS_SECRET_ACCESS_KEY="YOUR_SECRET_KEY"
export AWS_S3_BUCKET="your-bucket-name"

# Email / SMTP
export SPRING_MAIL_HOST="smtp.example.com"
export SPRING_MAIL_PORT=587
export SPRING_MAIL_USERNAME="your-email@example.com"
export SPRING_MAIL_PASSWORD="your-password"
```

### Storage Configuration

The server supports two storage backends:

#### Local Storage

Files are stored on the local filesystem:

```yaml
wutsi:
    platform:
        storage:
            type: local
```

#### AWS S3 Storage

Files are stored in Amazon S3:

```yaml
wutsi:
    platform:
        storage:
            type: s3
            s3:
                bucket: your-bucket-name
                region: us-east-1
                access-key: ${AWS_ACCESS_KEY}
                secret-key: ${AWS_SECRET_KEY}
```

### Email Configuration

Configure SMTP for sending emails:

```yaml
spring:
    mail:
        host: smtp.gmail.com
        port: 587
        username: your-email@gmail.com
        password: your-app-password
        properties:
            mail:
                smtp:
                    auth: true
                    starttls:
                        enable: true
```

For local development, you can disable email or use a test SMTP server.

### Cache Configuration

Redis caching (optional):

```yaml
wutsi:
    platform:
        cache:
            redis:
                url: redis://localhost:6379
```

## Running the Project

### Run via Maven (Development)

Run the application using Maven Spring Boot plugin:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

This runs the application directly without creating a JAR file.

### Run Packaged JAR

Build and run the packaged application:

```bash
mvn clean package
java -jar -Dspring.profiles.active=local target/koki-server-1.0.0-SNAPSHOT.jar
```

### Run with Environment Variables

```bash
export SPRING_PROFILES_ACTIVE=local
export SPRING_DATASOURCE_PASSWORD=secret
java -jar target/koki-server-1.0.0-SNAPSHOT.jar
```

### Verify the Application

Once the application starts, verify it's running:

#### Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{
    "status": "UP"
}
```

#### Info Endpoint

```bash
curl http://localhost:8080/actuator/info
```

#### Flyway Migrations Status

```bash
curl http://localhost:8080/actuator/flyway
```

### API Documentation

Access the interactive Swagger UI at:

```
http://localhost:8080/api.html
```

The API is organized into groups:

- **Authentication API** - `/v1/auth/*`
- **Account API** - `/v1/accounts/*`, `/v1/attributes/*`
- **Agent API** - `/v1/agents/*`
- **Contact API** - `/v1/contacts/*`
- **File API** - `/v1/files/*`
- **Lead API** - `/v1/leads/*`
- **Listing API** - `/v1/listings/*`
- **Message API** - `/v1/messages/*`
- **Offer API** - `/v1/offers/*`
- **Note API** - `/v1/notes/*`
- **User API** - `/v1/users/*`
- **Tenant API** - `/v1/tenants/*`

### Verify External Services

#### Check RabbitMQ

```bash
rabbitmqctl status
rabbitmqctl list_queues
```

RabbitMQ Management UI: http://localhost:15672

#### Check Redis

```bash
redis-cli ping
```

Expected response: `PONG`

## Running Tests

### Run All Tests

```bash
mvn test
```

This runs all unit and integration tests using Spring Boot Test framework.

### Run Specific Test Class

```bash
mvn test -Dtest=AccountServiceTest
```

### Run Tests Matching Pattern

```bash
mvn test -Dtest=*Account*
```

### Full Verification with Coverage

Run tests and generate coverage report:

```bash
mvn clean verify
```

This command:

1. Compiles the code
2. Runs all tests
3. Generates JaCoCo coverage report
4. Enforces coverage thresholds (92% line coverage, 92% class coverage)

The build will fail if coverage is below the threshold.

### View Coverage Report

After running `mvn verify`, open the coverage report:

```bash
# macOS
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

### Skip Tests (Quick Build)

To build without running tests:

```bash
mvn clean package -DskipTests
```

**Note**: This is useful for rapid development but should not be used before committing code.

### Test Configuration

Tests use the `test` profile with configuration in `application-test.yml`:

```yaml
spring:
    flyway:
        locations: "classpath:db/migration/common,classpath:db/migration/test"
    datasource:
        url: ${SPRING_DATABASE_URL}
        username: ${SPRING_DATABASE_USERNAME}
        password: ${SPRING_DATABASE_PASSWORD}
```

For CI/CD environments, set these environment variables:

- `SPRING_DATABASE_URL`
- `SPRING_DATABASE_USERNAME`
- `SPRING_DATABASE_PASSWORD`
- `CLOUDAMQP_URL` (RabbitMQ)
- `REDISCLOUD_URL` (Redis)

### Integration Tests

Integration tests use `@SpringBootTest` and require:

- Running MySQL database
- Active Spring application context
- Test data from migration scripts

Example integration test:

```kotlin
@SpringBootTest
@ActiveProfiles("test")
class AccountEndpointTest {

    @Autowired
    private lateinit var accountService: AccountService

    @Test
    fun `should create account`() {
        val request = CreateAccountRequest(name = "Test Account")
        val response = accountService.create(request)

        assertNotNull(response.id)
        assertEquals("Test Account", response.name)
    }
}
```

## Troubleshooting

### Common Issues

#### Database Connection Failures

**Error**: `Access denied for user 'root'@'localhost'`

**Solution**:

- Verify MySQL is running: `mysql --version`
- Check credentials in configuration
- Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`
- Ensure user has privileges on the `koki` database

#### Flyway Migration Errors

**Error**: `Migration checksum mismatch` or `Failed to execute migration`

**Solution**:

- Check migration file naming: `V<version>__<description>.sql`
- Verify migration ordering (newer versions must be higher)
- Clean database and restart: `DROP DATABASE koki; CREATE DATABASE koki;`
- Check Flyway history: `SELECT * FROM flyway_schema_history;`

#### Port Already in Use

**Error**: `Address already in use: bind :8080`

**Solution**:

- Change port: `export SERVER_PORT=8081` or update `application.yml`
- Find and stop conflicting process:
  ```bash
  # macOS/Linux
  lsof -i :8080
  kill -9 <PID>

  # Windows
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F
  ```

#### RabbitMQ Connection Refused

**Error**: `Connection refused` or `RabbitMQ is not available`

**Solution**:

- Start RabbitMQ: `docker start koki-rabbit` or `rabbitmq-server`
- Verify service: `rabbitmqctl status`
- Check connection URL: `export RABBITMQ_URL="amqp://localhost:5672"`

#### Redis Connection Issues

**Error**: `Cannot connect to Redis at localhost:6379`

**Solution**:

- Start Redis: `docker start koki-redis` or `redis-server`
- Verify: `redis-cli ping`
- Redis is optional; disable caching if not needed

#### Low Test Coverage

**Error**: `Rule violated for bundle koki-server: lines covered ratio is 0.90, but expected minimum is 0.92`

**Solution**:

- Write additional tests for uncovered code
- Identify gaps: Review JaCoCo report at `target/site/jacoco/index.html`
- Temporarily adjust threshold in `pom.xml` (not recommended):
  ```xml
  <jacoco.threshold.line>0.90</jacoco.threshold.line>
  ```

### Logging Configuration

Adjust log levels for debugging:

```yaml
logging:
    level:
        com.wutsi: DEBUG
        org.springframework.web: DEBUG
        org.hibernate.SQL: DEBUG
        org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Database Debugging

Enable SQL logging:

```yaml
spring:
    jpa:
        show-sql: true
        properties:
            hibernate:
                format_sql: true
```

### Check Application Logs

View recent logs:

```bash
tail -f logs/application.log
```

Or check console output when running via Maven or JAR.

## Next Steps

After successfully setting up koki-server:

1. **Explore the API**: Use Swagger UI at http://localhost:8080/api.html
2. **Import Sample Data**: Use CSV import endpoints for bulk data loading
3. **Integrate with SDK**: Use `koki-sdk` module to build client applications
4. **Configure Multi-Tenancy**: Set up additional tenants via the Tenant API
5. **Set Up Monitoring**: Configure actuator endpoints for production monitoring
6. **Deploy to Production**: Follow deployment guide for Heroku or AWS

## Additional Resources

- [README.md](README.md) - Project overview and features
- [CONTRIBUTING.md](../../CONTRIBUTING.md) - Contribution guidelines
- [koki-sdk](../koki-sdk/README.md) - Client SDK documentation
- [koki-dto](../koki-dto/README.md) - Data Transfer Objects
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

