# Setup Guide - koki-tracking-server

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)

## Prerequisites

Before running the koki-tracking-server, ensure you have the following installed and configured:

- **Java Development Kit (JDK) 17** or higher
    - Verify: `java -version`
    - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
      or [OpenJDK](https://openjdk.org/)

- **Maven 3.8+** for dependency management
    - Verify: `mvn -version`
    - Download from: [Maven](https://maven.apache.org/download.cgi)

- **RabbitMQ 4.0+** message broker for event consumption
    - Verify: `rabbitmqctl status`
    - Download from: [RabbitMQ](https://www.rabbitmq.com/download.html)

- **Redis 7.0+** for caching GeoIP lookups and reference data
    - Verify: `redis-cli ping`
    - Download from: [Redis](https://redis.io/download/)

### Optional Tools

- **Docker & Docker Compose** for containerized service dependencies
    - Download from: [Docker](https://www.docker.com/get-started)

- **AWS CLI** if using S3 storage backend
    - Download from: [AWS CLI](https://aws.amazon.com/cli/)

- **IntelliJ IDEA** or other Kotlin-compatible IDE for development

### Quick Start With Docker (Optional)

If you prefer to use Docker for dependencies:

```bash
# Start RabbitMQ with management UI
docker run -d --name koki-rabbit \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# Start Redis
docker run -d --name koki-redis \
  -p 6379:6379 \
  redis:7-alpine redis-server --requirepass test
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

### Build the Tracking Server Module

```bash
cd modules/koki-tracking-server
mvn clean package
```

The compiled JAR will be available at: `target/koki-tracking-server.jar`

## Database Setup

The koki-tracking-server does **not** use a traditional relational database. Instead, it uses:

- **CSV-based Storage**: Raw tracking events and KPI reports are stored as CSV files
- **Redis**: Used for caching GeoIP lookups and reference data to minimize external API calls

### Redis Setup

#### Using Docker

```bash
docker run -d --name koki-redis \
  -p 6379:6379 \
  redis:7-alpine redis-server --requirepass test
```

Verify Redis is running:

```bash
redis-cli -a test ping
```

Expected response: `PONG`

#### Native Installation

**macOS (Homebrew):**

```bash
brew install redis
brew services start redis
```

**Ubuntu/Debian:**

```bash
sudo apt-get update
sudo apt-get install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

**Configure Redis Password (Optional):**

Edit Redis configuration:

```bash
# macOS
sudo nano /usr/local/etc/redis.conf

# Linux
sudo nano /etc/redis/redis.conf
```

Add or uncomment:

```
requirepass test
```

Restart Redis:

```bash
# macOS
brew services restart redis

# Linux
sudo systemctl restart redis-server
```

### RabbitMQ Setup

#### Using Docker

```bash
docker run -d --name koki-rabbit \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

Access RabbitMQ Management Console: http://localhost:15672

- Default credentials: `guest` / `guest`

#### Native Installation

**macOS (Homebrew):**

```bash
brew install rabbitmq
brew services start rabbitmq
```

**Ubuntu/Debian:**

```bash
sudo apt-get update
sudo apt-get install rabbitmq-server
sudo systemctl start rabbitmq-server
sudo systemctl enable rabbitmq-server
```

**Enable Management Plugin:**

```bash
sudo rabbitmq-plugins enable rabbitmq_management
```

### Storage Setup

The module supports two storage backends:

#### Local Filesystem Storage (Default)

The application automatically creates the storage directory at:

```
${user.home}/__wutsi
```

For manual creation:

```bash
mkdir -p ${HOME}/__wutsi
chmod 755 ${HOME}/__wutsi
```

#### AWS S3 Storage (Production)

For production environments using AWS S3:

1. Create an S3 bucket in your AWS account
2. Obtain AWS credentials (Access Key ID and Secret Access Key)
3. Ensure the IAM user has permissions to read/write objects in the bucket
4. Configure the credentials in your application configuration (see Configuration section)

## Configuration

The application uses Spring Boot configuration with profile-specific overrides.

### Base Configuration

Default configuration is in `src/main/resources/application.yml`:

- Server port: `8083`
- Event buffer size: `10000`
- Flush schedule: Every 15 minutes
- KPI generation schedules
- RabbitMQ queue names
- Redis connection settings

### Profile-Specific Configuration

Create profile-specific configuration files for different environments:

#### Local Development (`application-local.yml`)

Create `src/main/resources/application-local.yml`:

```yaml
koki:
    persister:
        buffer-size: 10000                          # Event buffer size before flush
        cron: "0 */15 * * * *"                      # Flush schedule (every 15 minutes)

    kpi:
        listing:
            daily-cron: "0 */15 * * * *"              # Daily KPI generation schedule
            monthly-cron: "0 30 5 2 * *"              # Monthly KPI (2nd of month at 5:30 AM)

    module:
        tracking:
            mq:
                consumer-delay-seconds: 1               # Delay between message consumption
                queue: koki-tracking-queue              # Main tracking queue
                dlq: koki-tracking-dlq                  # Dead letter queue
                dlq-cron: "0 */15 * * * *"              # Process DLQ every 15 minutes

wutsi:
    platform:
        cache:
            name: koki
            type: redis
            ttl: 86400
            redis:
                url: redis://:test@localhost:6379      # Redis connection URL

        executor:
            thread-pool:
                name: koki-tracking
                size: 16

        mq:
            type: rabbitmq
            rabbitmq:
                url: amqp://localhost                   # RabbitMQ connection URL
                exchange-name: koki-tracking            # RabbitMQ exchange name
                max-retries: 24                         # Max retry attempts for failed messages
                ttl-seconds: 84600                      # Message TTL (23.5 hours)

        storage:
            enabled: true
            type: local                               # Storage type: "local" or "s3"
            local:
                directory: ${user.home}/__wutsi         # Local storage directory
                base-url: http://localhost:8083
                servlet-path: /local-storage

logging:
    level:
        com.wutsi: DEBUG
        org.springframework: INFO
```

#### Production Configuration

For production using AWS S3:

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

### Environment Variables

You can override configuration using environment variables:

```bash
# Redis
export REDIS_URL="redis://:test@localhost:6379"

# RabbitMQ
export RABBITMQ_URL="amqp://localhost"

# Storage Backend
export STORAGE_TYPE=local  # or s3
export STORAGE_LOCAL_DIRECTORY="${HOME}/__wutsi"

# AWS S3 (if using S3 storage)
export AWS_ACCESS_KEY_ID="YOUR_ACCESS_KEY"
export AWS_SECRET_ACCESS_KEY="YOUR_SECRET_KEY"
export AWS_S3_BUCKET="your-bucket-name"
export AWS_REGION="us-east-1"

# Server
export SERVER_PORT=8083
```

### Event Processing Configuration

#### Buffer and Flush Settings

Control event buffering and persistence:

```yaml
koki:
    persister:
        buffer-size: 10000                    # Number of events before auto-flush
        cron: "0 */15 * * * *"                # Flush every 15 minutes
```

#### KPI Generation Schedules

Configure when KPI aggregation jobs run:

```yaml
koki:
    kpi:
        listing:
            daily-cron: "0 */15 * * * *"        # Generate daily KPIs every 15 minutes (for dev)
            monthly-cron: "0 30 5 2 * *"        # Generate monthly KPIs on 2nd of month at 5:30 AM
```

#### Message Queue Configuration

Configure RabbitMQ message consumption:

```yaml
koki:
    module:
        tracking:
            mq:
                consumer-delay-seconds: 1         # Delay between message processing
                queue: koki-tracking-queue        # Main event queue
                dlq: koki-tracking-dlq            # Dead letter queue for failed messages
                dlq-cron: "0 */15 * * * *"        # Process DLQ every 15 minutes
                max-retries: 24                   # Maximum retry attempts
```

## Running the Project

### Start Required Services

Ensure RabbitMQ and Redis are running:

```bash
# Check Redis
redis-cli -a test ping

# Check RabbitMQ
rabbitmqctl status
```

If using Docker:

```bash
docker ps | grep redis
docker ps | grep rabbitmq
```

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
java -jar -Dspring.profiles.active=local target/koki-tracking-server.jar
```

### Run with Environment Variables

```bash
export SPRING_PROFILES_ACTIVE=local
export REDIS_URL="redis://:test@localhost:6379"
export RABBITMQ_URL="amqp://localhost"
java -jar target/koki-tracking-server.jar
```

### Verify the Application

Once the application starts, verify it's running:

#### Health Check

```bash
curl http://localhost:8083/actuator/health
```

Expected response:

```json
{
    "status": "UP",
    "components": {
        "diskSpace": {
            "status": "UP"
        },
        "ping": {
            "status": "UP"
        },
        "redis": {
            "status": "UP"
        }
    }
}
```

#### Info Endpoint

```bash
curl http://localhost:8083/actuator/info
```

#### Scheduled Tasks

View all scheduled tasks (persister, KPI generation, DLQ processing):

```bash
curl http://localhost:8083/actuator/scheduledtasks
```

### API Documentation

Access the interactive Swagger UI at:

```
http://localhost:8083/swagger-ui.html
```

### Verify External Services

#### Check RabbitMQ

```bash
# Status
rabbitmqctl status

# List queues
rabbitmqctl list_queues

# Management UI
open http://localhost:15672
```

#### Check Redis

```bash
# Ping
redis-cli -a test ping

# Monitor commands
redis-cli -a test monitor

# Check cache keys
redis-cli -a test keys "*"
```

### Verify Event Processing

To test event processing:

1. **Publish a test event** to the `koki-tracking-queue` via RabbitMQ Management UI
2. **Check logs** for event consumption and enrichment
3. **Verify storage** - CSV files should be created in the storage directory after flush

Example test event payload:

```json
{
    "tenantId": 1,
    "time": "2025-11-17T10:00:00Z",
    "type": "impression",
    "productId": "123",
    "page": "/listings/123",
    "referrer": "https://google.com",
    "userAgent": "Mozilla/5.0...",
    "ip": "8.8.8.8",
    "ua": "desktop",
    "deviceType": "desktop"
}
```

## Running Tests

### Run All Tests

```bash
mvn test
```

This runs all unit and integration tests using Spring Boot Test framework.

### Run Specific Test Class

```bash
mvn test -Dtest=TrackingConsumerTest
```

### Run Tests Matching Pattern

```bash
mvn test -Dtest=*Filter*
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
4. Enforces coverage thresholds (97% line coverage, 91% class coverage)

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
wutsi:
    platform:
        cache:
            redis:
                url: ${REDISCLOUD_URL}
        mq:
            rabbitmq:
                url: ${CLOUDAMQP_URL}
```

For CI/CD environments, set these environment variables:

- `REDISCLOUD_URL`
- `CLOUDAMQP_URL`

### Integration Tests

Integration tests verify:

- Event consumption from RabbitMQ
- Enrichment pipeline processing
- CSV file generation
- KPI aggregation
- Dead letter queue handling

Example integration test:

```kotlin
@SpringBootTest
@ActiveProfiles("test")
class TrackingConsumerTest {

    @Autowired
    private lateinit var trackingConsumer: TrackingConsumer

    @Test
    fun `should consume and enrich tracking event`() {
        val event = TrackEvent(
            tenantId = 1,
            type = "impression",
            productId = "123"
        )

        trackingConsumer.consume(event)

        // Verify event was enriched and buffered
    }
}
```

## Troubleshooting

### Common Issues

#### Redis Connection Failed

**Error**: `Unable to connect to Redis at localhost:6379`

**Solution**:

- Verify Redis is running: `redis-cli -a test ping`
- Check Redis URL in configuration
- Verify password if using authentication
- If using Docker, ensure port 6379 is mapped correctly
- Check Redis logs: `docker logs koki-redis`

#### RabbitMQ Connection Failed

**Error**: `Connection refused to amqp://localhost`

**Solution**:

- Verify RabbitMQ is running: `rabbitmqctl status`
- Check RabbitMQ URL in configuration
- Ensure port 5672 is accessible
- Check firewall settings
- View RabbitMQ logs: `docker logs koki-rabbit`

#### Storage Directory Permission Denied

**Error**: `Permission denied: ${user.home}/__wutsi`

**Solution**:

```bash
mkdir -p ${HOME}/__wutsi
chmod 755 ${HOME}/__wutsi
```

Or specify a different directory with write permissions:

```yaml
wutsi:
    platform:
        storage:
            local:
                directory: /tmp/__wutsi
```

#### Port 8083 Already in Use

**Error**: `Address already in use: bind :8083`

**Solution**:

- Change port: `export SERVER_PORT=8084` or update `application.yml`
- Find and stop conflicting process:
  ```bash
  # macOS/Linux
  lsof -i :8083
  kill -9 <PID>

  # Windows
  netstat -ano | findstr :8083
  taskkill /PID <PID> /F
  ```

#### Maven Build Fails

**Error**: `Could not resolve dependencies`

**Solution**:

```bash
# Clean Maven cache
rm -rf ~/.m2/repository/com/wutsi/koki

# Rebuild from root
cd ../..
mvn clean install -DskipTests
cd modules/koki-tracking-server
mvn clean package
```

#### Events Not Being Consumed

**Error**: No events being processed

**Solution**:

- Verify queue exists in RabbitMQ: `rabbitmqctl list_queues`
- Check queue name matches configuration
- Ensure messages are being published to the correct exchange
- Check consumer delay settings: `consumer-delay-seconds`
- Review application logs for errors

#### Low Test Coverage

**Error**: `Rule violated for bundle koki-tracking-server: lines covered ratio is 0.95, but expected minimum is 0.97`

**Solution**:

- Write additional tests for uncovered code
- Identify gaps: Review JaCoCo report at `target/site/jacoco/index.html`
- Focus on service and filter classes
- Temporarily adjust threshold in `pom.xml` (not recommended):
  ```xml
  <jacoco.threshold.line>0.95</jacoco.threshold.line>
  ```

### Logging Configuration

Adjust log levels for debugging:

```yaml
logging:
    level:
        com.wutsi: DEBUG
        com.wutsi.koki.tracking.server: TRACE
        org.springframework.amqp: DEBUG
```

### Monitoring Event Processing

Enable detailed logging for tracking events:

```yaml
logging:
    level:
        com.wutsi.koki.tracking.server.service.TrackingConsumer: DEBUG
        com.wutsi.koki.tracking.server.service.filter: DEBUG
```

### Check Application Logs

View recent logs:

```bash
tail -f logs/application.log
```

Or check console output when running via Maven or JAR.

### Verify CSV File Generation

Check storage directory for generated files:

```bash
ls -lh ${HOME}/__wutsi/track/
ls -lh ${HOME}/__wutsi/kpi/listing/
```

Files should be generated after buffer flush or scheduled job execution.

## Next Steps

After successfully setting up koki-tracking-server:

1. **Integrate with koki-server**: Configure koki-server to publish tracking events to RabbitMQ
2. **Monitor Event Flow**: Watch logs to see events being consumed and enriched
3. **Review Generated Reports**: Check CSV files in storage directory for KPI data
4. **Configure Production Settings**: Set up AWS S3, adjust buffer sizes and cron schedules
5. **Set Up Monitoring**: Configure alerting for failed events in DLQ
6. **Scale Horizontally**: Deploy multiple instances for high-volume event processing

## Additional Resources

- [README.md](README.md) - Project overview and architecture
- [CONTRIBUTING.md](../../CONTRIBUTING.md) - Contribution guidelines
- [koki-server](../koki-server/README.md) - Main server that publishes tracking events
- [koki-dto](../koki-dto/README.md) - Data Transfer Objects
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [Redis Documentation](https://redis.io/documentation)
- [UAParser Documentation](https://github.com/ua-parser/uap-java)

