# Setup Guide - koki-tracking-server

This guide provides detailed instructions for setting up the koki-tracking-server module for local development.

## Prerequisites

Before you begin, ensure you have the following software and tools installed:

### Required Software

- **Java Development Kit (JDK) 17** or higher
    - Verify installation: `java -version`
    - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)or [OpenJDK](https://openjdk.org/)

- **Apache Maven 3.8+**
    - Verify installation: `mvn -version`
    - Download from: [Apache Maven](https://maven.apache.org/download.cgi)

- **RabbitMQ 4.0+**
    - Required for message queue consumption
    - Verify installation: `rabbitmqctl status`
    - Download from: [RabbitMQ Downloads](https://www.rabbitmq.com/download.html)

- **Redis 7.0+**
    - Required for caching GeoIP lookups and reference data
    - Verify installation: `redis-cli ping` (should return `PONG`)
    - Download from: [Redis Downloads](https://redis.io/download)

### Optional Software

- **Docker** and **Docker Compose** (recommended for running RabbitMQ and Redis)
    - Download from: [Docker Desktop](https://www.docker.com/products/docker-desktop)

- **AWS CLI** (if using AWS S3 for storage)
    - Download from: [AWS CLI](https://aws.amazon.com/cli/)

## Installation

Follow these steps to install and build the project locally.

### 1. Clone the Repository

```bash
git clone https://github.com/wutsi/koki-mono.git
cd koki-mono
```

### 2. Build the Parent Project

Build the entire monorepo from the root directory:

```bash
mvn clean install
```

This will build all modules including dependencies (`koki-dto`, `koki-platform`) required by `koki-tracking-server`.

### 3. Navigate to the Module

```bash
cd modules/koki-tracking-server
```

### 4. Verify the Build

```bash
mvn clean package
```

The build should complete successfully and create `target/koki-tracking-server.jar`.

## Database Setup

**Note:** The koki-tracking-server module does **not** use a traditional database. Instead, it uses:

- **CSV-based Storage**: Raw tracking events and KPI reports are stored as CSV files
- **Redis**: Used for caching GeoIP lookups and reference data

### Redis Setup

If you don't have Redis running, you can start it using Docker:

```bash
docker run -d \
  --name redis-koki \
  -p 6379:6379 \
  redis:7-alpine redis-server --requirepass test
```

Or install Redis locally:

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
```

### RabbitMQ Setup

Start RabbitMQ using Docker:

```bash
docker run -d \
  --name rabbitmq-koki \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

Or install RabbitMQ locally:

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
```

Access RabbitMQ Management Console at: `http://localhost:15672`

- Default credentials: `guest` / `guest`

### Storage Setup

The module supports two storage backends:

#### Local Filesystem Storage (Default for Development)

The application will automatically create the storage directory at:

```
${user.home}/__wutsi
```

No additional setup required.

#### AWS S3 Storage (Production)

If you plan to use AWS S3:

1. Create an S3 bucket in your AWS account
2. Obtain AWS credentials (Access Key ID and Secret Access Key)
3. Configure the credentials in `application-local.yml` (see Configuration section)

## Configuration

Create a local configuration file for development settings.

### 1. Create Local Configuration File

Create the file `src/main/resources/application-local.yml`:

```bash
touch src/main/resources/application-local.yml
```

### 2. Configure Application Settings

Add the following configuration to `application-local.yml`:

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

            # Uncomment and configure if using AWS S3
            # s3:
            #   bucket: your-bucket-name
            #   region: us-east-1
            #   access-key: YOUR_AWS_ACCESS_KEY
            #   secret-key: YOUR_AWS_SECRET_KEY
```

### Configuration Sections Explained

- **persister**: Controls event buffering and flush frequency to CSV storage
- **kpi.listing**: Schedules for daily and monthly KPI aggregation jobs
- **module.tracking.mq**: RabbitMQ queue configuration, DLQ handling, and retry policies
- **platform.cache**: Redis configuration for caching GeoIP lookups
- **platform.mq**: RabbitMQ connection settings and message retry configuration
- **platform.storage**: Storage backend configuration (local filesystem or AWS S3)

### Environment Variables (Alternative)

You can also configure the application using environment variables:

```bash
export REDIS_URL=redis://:test@localhost:6379
export RABBITMQ_URL=amqp://localhost
export STORAGE_TYPE=local
export STORAGE_LOCAL_DIRECTORY=${HOME}/__wutsi
```

## Running the Project

### 1. Start Required Services

Ensure RabbitMQ and Redis are running:

```bash
# Check Redis
redis-cli ping

# Check RabbitMQ
rabbitmqctl status
```

If using Docker:

```bash
docker ps | grep redis
docker ps | grep rabbitmq
```

### 2. Run the Application

Run the application with the `local` profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Or run from the JAR:

```bash
mvn clean package
java -jar -Dspring.profiles.active=local target/koki-tracking-server.jar
```

### 3. Verify the Application is Running

The server will start on port **8083** by default.

Check the health endpoint:

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

### 4. Access API Documentation

Open your browser and navigate to:

```
http://localhost:8083/swagger-ui.html
```

This provides interactive API documentation powered by SpringDoc OpenAPI.

### 5. View Application Info

Check application information and Git details:

```bash
curl http://localhost:8083/actuator/info
```

### 6. Monitor Scheduled Tasks

View all scheduled tasks:

```bash
curl http://localhost:8083/actuator/scheduledtasks
```

## Running Tests

### Run All Tests

Execute all unit and integration tests:

```bash
mvn test
```

### Run Tests with Coverage Report

Generate JaCoCo code coverage report:

```bash
mvn clean verify
```

The coverage report will be generated at:

```
target/site/jacoco/index.html
```

Open it in your browser:

```bash
open target/site/jacoco/index.html    # macOS
xdg-open target/site/jacoco/index.html # Linux
```

### Run Specific Test Classes

```bash
mvn test -Dtest=TrackingConsumerTest
```

### Run Tests in Watch Mode

For continuous testing during development:

```bash
mvn test -Dsurefire.failIfNoSpecifiedTests=false -DfailIfNoTests=false
```

### Coverage Thresholds

The project enforces the following JaCoCo coverage thresholds:

- **Line Coverage**: 97%
- **Class Coverage**: 91%

If coverage falls below these thresholds, the build will fail.

## Troubleshooting

### Common Issues

#### 1. Redis Connection Failed

**Error:** `Unable to connect to Redis at localhost:6379`

**Solution:**

- Verify Redis is running: `redis-cli ping`
- Check Redis URL in configuration matches your setup
- If using Docker, ensure port 6379 is mapped correctly

#### 2. RabbitMQ Connection Failed

**Error:** `Connection refused to amqp://localhost`

**Solution:**

- Verify RabbitMQ is running: `rabbitmqctl status`
- Check RabbitMQ URL in configuration
- Ensure port 5672 is accessible
- Check RabbitMQ logs: `docker logs rabbitmq-koki`

#### 3. Storage Directory Permission Denied

**Error:** `Permission denied: ${user.home}/__wutsi`

**Solution:**

```bash
mkdir -p ${HOME}/__wutsi
chmod 755 ${HOME}/__wutsi
```

#### 4. Port 8083 Already in Use

**Error:** `Port 8083 is already in use`

**Solution:**

- Stop the process using port 8083
- Or change the port in configuration:
  ```yaml
  server:
    port: 8084
  ```

#### 5. Maven Build Fails

**Error:** `Could not resolve dependencies`

**Solution:**

```bash
# Clean Maven cache
rm -rf ~/.m2/repository/com/wutsi/koki

# Rebuild from root
cd ../..
mvn clean install -DskipTests
cd modules/koki-tracking-server
mvn clean install
```

### Logs

Application logs are written to the console. To view logs:

```bash
# When running with maven
mvn spring-boot:run -Dspring-boot.run.profiles=local | grep "com.wutsi"

# When running from JAR
java -jar target/koki-tracking-server.jar | tee application.log
```

Log levels can be adjusted in `application-local.yml`:

```yaml
logging:
    level:
        com.wutsi.koki: DEBUG
        org.springframework: INFO
```

## Next Steps

After completing the setup:

1. **Review the Architecture**: Read the [High-Level Architecture](README.md#high-level-architecture) section in the
   README
2. **Explore the API**: Use the Swagger UI to understand available endpoints
3. **Send Test Events**: Publish test tracking events to RabbitMQ
4. **Monitor Event Processing**: Check logs for event consumption and enrichment
5. **View Generated KPIs**: Check the storage directory for generated CSV reports

## Additional Resources

- [README.md](README.md) - Project overview and features
- [CONTRIBUTING.md](../../CONTRIBUTING.md) - Contribution guidelines
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [Redis Documentation](https://redis.io/documentation)

