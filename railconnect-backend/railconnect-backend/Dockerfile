# syntax=docker/dockerfile:1

# ---- Build stage ----
# Builds the jar inside the image so the image is reproducible from source alone; no local
# `mvn package` step required before docker build.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# Cache Maven dependencies in their own layer - only re-downloaded when pom.xml changes.
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests package

# ---- Runtime stage ----
# Slim JRE-only image - the JDK/Maven from the build stage aren't shipped.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# curl is needed for the HEALTHCHECK below; not present in the base image by default.
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Run as a non-root user.
RUN groupadd -r railconnect && useradd -r -g railconnect railconnect

COPY --from=build /workspace/target/*.jar app.jar
RUN chown railconnect:railconnect app.jar
USER railconnect

EXPOSE 8080

# Actuator health endpoint doubles as the container healthcheck.
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
