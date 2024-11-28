# Docker Instructions for Spring Boot Application

## Prerequisites
- Docker installed on your system
- Java project with Gradle build
- Source code available locally

## Project Structure
```
project-root/
├── src/
├── config/              # Directory for logging configuration
│   └── logback-spring.xml
├── Dockerfile
├── docker-compose.yml
├── build.gradle.kts
└── settings.gradle.kts
```

## Step-by-Step Instructions

### 1. Create Config Directory
```bash
# Create config directory
mkdir -p config

# Copy logging configuration
cp src/main/resources/logback-spring.xml config/
```

### 2. Building and Running

#### Option 1: Using Docker Compose (Recommended)
```bash
# Build and start containers
docker compose up --build

# Or run in detached mode
docker compose up -d --build

# View logs
docker compose logs -f

# Stop and remove containers
docker compose down

# Stop and remove containers, images, and volumes
docker compose down --rmi all -v
```

#### Option 2: Using Docker Commands
```bash
# Build the image
docker build -t spring-boot-kubernetes:0.0.1 .

# Build the image with verbose output
docker build --no-cache --progress=plain -t spring-boot-kubernetes:0.0.1 .

# Run the container
docker run \
  --name spring-boot-app \
  -p 8080:8080 \
  -v "$(pwd)/config:/app/logs" \
  -v "$(pwd)/config:/app/config" \
  -e LOGBACK_CONFIG_PATH=/app/config \
  -e LOG_PATH=/app/logs \
  spring-boot-kubernetes:0.0.1
```

## Docker Compose Commands

### Service Management
```bash
# Start services
docker compose up -d

# Stop services
docker compose stop

# Start specific service
docker compose up -d spring-boot-app

# Rebuild and restart specific service
docker compose up --build -d spring-boot-app

# View service logs
docker compose logs -f spring-boot-app

# Execute command in container
docker compose exec spring-boot-app /bin/sh
```

### Cleanup Commands
```bash
# Remove containers
docker compose down

# Remove containers and images
docker compose down --rmi all

# Remove containers, images, and volumes
docker compose down --rmi all -v
```

## Docker Commands (Alternative)

### Container Management
```bash
# Check container logs
docker logs -f spring-boot-app

# Stop container
docker stop spring-boot-app

# Remove container
docker rm spring-boot-app

# Remove container and its volumes
docker rm -v spring-boot-app
```

### Image Management
```bash
# List images
docker images

# Remove image
docker rmi spring-boot-kubernetes:0.0.1
```

### Volume Management
```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect [volume-name]
```

## Accessing the Application
- The application will be available at: `http://localhost:8080`
- Health check endpoint: `http://localhost:8080/actuator/health`

## Log Files
- Logs are available in the `config` directory on your host machine
- You can tail the logs using: `tail -f config/application.log`

## Troubleshooting

### Container won't start
1. Check container logs:
```bash
# Using Docker Compose
docker compose logs spring-boot-app

# Using Docker
docker logs spring-boot-app
```

### Permission issues
1. Verify directory permissions:
```bash
ls -la config/
```
2. Ensure the spring user (created in Dockerfile) has write permissions

### Port conflicts
If port 8080 is already in use:
1. Stop the conflicting service, or
2. Modify the port mapping in `docker-compose.yml`:
```yaml
services:
  spring-boot-app:
    ports:
      - "8081:8080"  # Changed host port to 8081
```

Or if using Docker run command:
```bash
docker run -d \
  --name spring-boot-app \
  -p 8081:8080 \  # Changed host port to 8081
  -v "$(pwd)/config:/app/logs" \
  -v "$(pwd)/config:/app/config" \
  -e LOGBACK_CONFIG_PATH=/app/config \
  -e LOG_PATH=/app/logs \
  spring-boot-kubernetes:0.0.1
```

### Memory Issues
1. Check container memory usage:
```bash
docker stats spring-boot-app
```

2. Adjust memory limits in `docker-compose.yml`:
```yaml
services:
  spring-boot-app:
    deploy:
      resources:
        limits:
          memory: 1G  # Increase memory limit
        reservations:
          memory: 512M  # Increase memory reservation
```