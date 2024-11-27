# Build stage
FROM gradle:jdk21-jammy AS build
WORKDIR /app

# Copy only the build files first
COPY build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle
COPY gradlew /app/

# Download dependencies first - this layer will be cached
RUN gradle dependencies --no-daemon

COPY ./JmxInvoke.java .
RUN javac JmxInvoke.java

# Copy the source code
COPY src /app/src

# Build the application
RUN gradle build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create necessary directories with appropriate permissions
RUN mkdir -p /app/logs /app/config && \
    useradd -r spring && \
    chown -R spring:spring /app

# Copy the jar from build stage
COPY --from=build --chown=spring:spring /app/build/libs/*.jar app.jar

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Switch to non-root user
USER spring:spring

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

COPY ./entrypoint.sh .

# Copy the compiled JmxInvoke.class from the build stage
COPY --from=build /app/JmxInvoke.class .

# Expose port
EXPOSE 8080
EXPOSE 9010

HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
CMD ["sh", "entrypoint.sh"]