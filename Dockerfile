# Build stage
FROM gradle:jdk21-jammy AS build
WORKDIR /app

# Copy only the build files first
COPY build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle
COPY gradlew /app/

# Download dependencies first - this layer will be cached
RUN gradle dependencies --no-daemon

# Copy the source code
COPY src /app/src

# Build the application
RUN gradle build jmxJar -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create necessary directories with appropriate permissions
RUN mkdir -p /app/logs /app/config && \
    useradd -r spring && \
    chown -R spring:spring /app

# Copy the jars from build stage
COPY --from=build --chown=spring:spring /app/build/libs/*.jar /app/

# Rename the jars
RUN mv /app/spring-boot-kubernetes-*.jar /app/app.jar && \
    mv /app/jmx-invoke-*.jar /app/jmx-invoke.jar

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Switch to non-root user
USER spring:spring

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

COPY --chown=spring:spring ./entrypoint.sh .

# Expose port
EXPOSE 8080 9010

HEALTHCHECK --interval=30s --timeout=3s \
  CMD java -jar jmx-invoke.jar "service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi" "spring-boot-kubernetes:type=Endpoint,name=Health" "health"
#  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
CMD ["sh", "entrypoint.sh"]