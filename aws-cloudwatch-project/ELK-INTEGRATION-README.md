# MovieBuffs ELK Stack Integration Guide

## Overview

This guide explains how to connect MovieBuffs application to the ELK (Elasticsearch, Logstash, Kibana) stack for centralized logging, monitoring, and log analysis.

## ELK Stack Components

- **Elasticsearch **: Document-oriented search and analytics engine for storing logs
- **Logstash **: Data processing pipeline for ingesting, transforming, and forwarding logs
- **Kibana **: Web interface for visualizing and exploring log data

## Prerequisites

- Docker and Docker Compose installed
- MovieBuffs application (can run locally in IDE or via Docker)
- Ports 5000, 5601, 9200, 9300 available

## ELK Stack Configuration

### 1. Docker Compose Setup

The ELK stack is configured in `docker/docker-compose-elk.yml`:

```yaml
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:9.0.3
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xmx1g -Xms1g"
    ports:
      - "9200:9200"
      - "9300:9300"

  logstash:
    image: docker.elastic.co/logstash/logstash:9.0.3
    container_name: logstash
    command: logstash -f /etc/logstash/conf.d/logstash.conf
    ports:
      - "5000:5000"

  kibana:
    image: docker.elastic.co/kibana/kibana:9.0.3
    container_name: kibana
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
      - xpack.security.enabled=false
    ports:
      - "5601:5601"
```

### 2. Logstash Configuration

The Logstash pipeline is configured in `config/elk/logstash.conf`:

```ruby
input {
  tcp {
    port => 5000
    codec => json_lines
  }
}

output {
  elasticsearch {
    hosts => "elasticsearch:9200"
    index => "moviebuffs"
  }
}
```

### 3. Application Logging Configuration

The application uses Logback with JSON formatting configured in `src/main/resources/logback-spring.xml`:

```xml
<appender class="net.logstash.logback.appender.LogstashTcpSocketAppender" name="stash">
    <destination>${logstashHost}:5000</destination>
    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <mdc/>
            <version/>
            <logLevel/>
            <loggerName/>
            <pattern>
                <pattern>
                    {
                    "app-name": "moviebuffs",
                    "app-version": "1.0"
                    }
                </pattern>
            </pattern>
            <threadName/>
            <message/>
            <logstashMarkers/>
            <arguments/>
            <stackTrace/>
        </providers>
    </encoder>
</appender>
```

## Getting Started

### Option 1: Full Docker Setup

1. **Start ELK stack with application:**
   ```bash
   cd c:\tools\git\moviebuffs
   ./run.sh start_all
   ```

### Option 2: ELK Stack + Local Application (Recommended for Development)

1. **Start only ELK stack:**
   ```bash
   cd c:\tools\git\moviebuffs\docker
   docker-compose -f docker-compose-elk.yml up -d
   ```

2. **Verify ELK services are running:**
   ```bash
   docker-compose -f docker-compose-elk.yml ps
   ```

3. **Start MovieBuffs application locally:**
   - **Option A: Using IDE**
     - Use the VS Code launch configuration: "Spring Boot-MovieBuffsApplication<moviebuffs-service>"
     - Make sure profile is set to `local` (already configured in launch.json)
   
   - **Option B: Using Maven**
     ```bash
     cd c:\tools\git\moviebuffs\moviebuffs-service
     ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
     ```

4. **Configure application properties for ELK:**
   
   The application is already configured with these properties in `application.properties`:
   ```properties
   application.logstash-host=localhost
   ```
   
   And in `logback-spring.xml`, logs are sent to Logstash when using `local` or `docker` profiles.

## Testing ELK Integration

### 1. Verify Service Health

- **Elasticsearch**: http://localhost:9200
  ```bash
  curl http://localhost:9200/_cluster/health
  ```

- **Kibana**: http://localhost:5601

### 2. Generate Application Logs

Execute these API calls to generate logs:

#### A. Movies API (No Authentication Required)

```bash
# Get all movies
curl "http://localhost:8080/api/movies"

# Search movies
curl "http://localhost:8080/api/movies?query=hero"

# Get movies by genre
curl "http://localhost:8080/api/movies?genre=action"

# Get specific movie
curl "http://localhost:8080/api/movies/1"

# Get all genres
curl "http://localhost:8080/api/genres"
```

#### B. User Authentication APIs

```bash
# Create a new user
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "testuser@example.com",
    "password": "password123"
  }'

# Login
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@gmail.com",
    "password": "admin"
  }'
```

#### C. Generate Error Logs

```bash
# Try to access non-existent movie (generates 404 error)
curl "http://localhost:8080/api/movies/999999"

# Try to access protected endpoint without authentication (generates 403 error)
curl "http://localhost:8080/api/orders"
```

### 3. Using Web Interface

1. **Open the application**: http://localhost:8080
2. **Navigate through pages:**
   - Browse movies
   - Search for movies
   - Filter by genres
   - Try to access protected areas without login

### 4. Check Logs in Elasticsearch

```bash
# Check if moviebuffs index exists
curl "http://localhost:9200/_cat/indices?v"

# View sample logs
curl "http://localhost:9200/moviebuffs/_search?pretty&size=5"

# Search for specific log levels
curl "http://localhost:9200/moviebuffs/_search?pretty" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "match": {
        "level": "ERROR"
      }
    },
    "size": 10
  }'
```

## Kibana Dashboard Setup

### 1. Access Kibana

Open http://localhost:5601 in your browser.

### 2. Create Index Pattern

1. Go to **Management** > **Stack Management** > **Index Patterns**
2. Click **Create index pattern**
3. Enter `moviebuffs*` as the index pattern
4. Select `@timestamp` as the time field
5. Click **Create index pattern**

### 3. Explore Logs

1. Go to **Analytics** > **Discover**
2. Select the `moviebuffs*` index pattern
3. You should see incoming logs from the application

### 4. Create Visualizations

#### Log Level Distribution
1. Go to **Analytics** > **Visualizations** > **Create visualization**
2. Choose **Pie chart**
3. Select `moviebuffs*` index
4. In Buckets, add **Split slices**:
   - Aggregation: Terms
   - Field: level.keyword
   - Size: 10

#### API Endpoint Usage
1. Create a **Bar chart**
2. Y-axis: Count
3. X-axis: 
   - Aggregation: Terms
   - Field: message.keyword
   - Filter for messages containing "API"

#### Error Logs Over Time
1. Create a **Line chart**
2. Y-axis: Count
3. X-axis: Date histogram on @timestamp
4. Add filter: level.keyword = "ERROR"

### 5. Create Dashboard

1. Go to **Analytics** > **Dashboard** > **Create dashboard**
2. Add your created visualizations
3. Save the dashboard as "MovieBuffs Application Monitoring"

## Useful Kibana Queries

### Filter by Log Level
```
level: "ERROR"
level: "WARN"
level: "INFO"
```

### Filter by Logger
```
logger_name: "com.sivalabs.moviebuffs.web.api.MoviesRestController"
logger_name: "com.sivalabs.moviebuffs.web.api.OrderRestController"
```

### Filter by Application Component
```
app-name: "moviebuffs"
```

### Search for API Calls
```
message: "API Fetching movies"
message: "Getting order by id"
```

### Find Exceptions
```
stack_trace: *
```

## Structured Logging Examples

The application uses structured logging with specific patterns:

### API Controller Logs
```json
{
  "@timestamp": "2025-06-28T10:30:00.000Z",
  "level": "INFO",
  "logger_name": "com.sivalabs.moviebuffs.web.api.MoviesRestController",
  "message": "API Fetching movies for page : 0, query: hero, genre: null",
  "app-name": "moviebuffs",
  "app-version": "1.0"
}
```

### Service Layer Logs
```json
{
  "@timestamp": "2025-06-28T10:30:01.000Z",
  "level": "DEBUG",
  "logger_name": "com.sivalabs.moviebuffs.core.service.MovieService",
  "message": "Searching movies with query: hero",
  "app-name": "moviebuffs"
}
```

### Error Logs
```json
{
  "@timestamp": "2025-06-28T10:30:02.000Z",
  "level": "ERROR",
  "logger_name": "com.sivalabs.moviebuffs.web.api.advice.GlobalExceptionHandler",
  "message": "Movie not found with id=999999",
  "stack_trace": "...",
  "app-name": "moviebuffs"
}
```

## Troubleshooting

### Common Issues

1. **No logs appearing in Kibana:**
   - Check if Logstash is receiving logs: `docker logs logstash`
   - Verify application profile includes `local` for Logstash appender
   - Check Elasticsearch health: `curl http://localhost:9200/_cluster/health`

2. **Connection refused to Logstash:**
   - Ensure Logstash container is running: `docker ps`
   - Check port 5000 is not blocked by firewall
   - Verify `application.logstash-host=localhost` in properties

3. **Kibana not accessible:**
   - Check Kibana logs: `docker logs kibana`
   - Ensure Elasticsearch is running and healthy
   - Wait for Kibana to fully start (can take 1-2 minutes)

### Debugging Commands

```bash
# Check ELK stack status
docker-compose -f docker-compose-elk.yml ps

# View logs from services
docker logs elasticsearch
docker logs logstash
docker logs kibana

# Check Elasticsearch indices
curl "http://localhost:9200/_cat/indices?v"

# Check Logstash pipeline
curl "http://localhost:9600/_node/stats/pipelines?pretty"
```

## Performance Considerations

- **Memory**: ELK stack uses significant memory (configured for 1GB for Elasticsearch)
- **Disk Space**: Logs accumulate over time; consider retention policies
- **Network**: JSON logging increases log size compared to plain text

## Stopping Services

```bash
# Stop ELK stack only
cd c:\tools\git\moviebuffs\docker
docker-compose -f docker-compose-elk.yml down

# Stop all services (if using run.sh start_all)
cd c:\tools\git\moviebuffs
./run.sh stop_all
```

## Next Steps

1. **Set up log retention policies** in Elasticsearch
2. **Create alerts** for critical errors using Kibana Watcher
3. **Add application metrics** using Micrometer and Prometheus
4. **Implement log aggregation** for multiple application instances
5. **Configure security** for production environments

## Additional Resources

- [Elastic Stack Documentation](https://www.elastic.co/guide/index.html)
- [Logback Logstash Encoder](https://github.com/logfellow/logstash-logback-encoder)
- [Spring Boot Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
