# Multi-stage build for Spring Boot application
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/target/backend.inventory-*.jar app.jar

# Create non-root user for security
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app && \
    apk add --no-cache netcat-openbsd

USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Run the application with Railway profile and wait for DB
ENTRYPOINT ["/bin/sh", "-c", "if [ -z \"$PGHOST\" ]; then if [ -n \"$RAILWAY_PRIVATE_DOMAIN\" ]; then export PGHOST=$RAILWAY_PRIVATE_DOMAIN; elif [ -n \"$DATABASE_URL\" ]; then clean_url=${DATABASE_URL#*@}; export PGHOST=${clean_url%%:*}; clean_port=${clean_url#*:}; export PGPORT=${clean_port%%/*}; fi; fi; if [ -n \"$PGHOST\" ]; then echo \"Connecting to database at $PGHOST:${PGPORT:-5432}\"; while ! nc -z $PGHOST ${PGPORT:-5432}; do sleep 1; done; echo 'Database is up!'; else echo 'WARNING: PGHOST is empty. Skipping DB readiness check.'; fi; java -Dspring.profiles.active=railway -jar app.jar"]
