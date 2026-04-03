# Step 1: Build the application using Maven and Eclipse Temurin JDK 25
FROM maven:3.9.9-eclipse-temurin-25 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml to download dependencies first (leveraging Docker layer caching)
COPY pom.xml .

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the source code and necessary configuration files
COPY src ./src
COPY api ./api

# Build the application
# We skip tests to speed up the build process in the container
RUN mvn clean package -DskipTests

# Step 2: Create the final runtime image using Eclipse Temurin JRE 25
FROM eclipse-temurin:25-jre

# Set the working directory for the runtime container
WORKDIR /app

# Copy the built jar file from the build stage
# The jar file name is based on artifactId and version from pom.xml
COPY --from=build /app/target/spring-mem-1.0-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Configure health check for the Spring Boot application (optional but recommended)
# This assumes spring-boot-starter-actuator is present in pom.xml
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Define the entry point for running the application
ENTRYPOINT ["java", "-jar", "app.jar"]
