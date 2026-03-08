# Use Maven with Java 25 for building
FROM maven:3.9.9-eclipse-temurin-25 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and API definitions
COPY src ./src
COPY api ./api

# Build the application
RUN mvn clean package -DskipTests

# Use a smaller runtime image for the final image
# Alpine image for Java 25 might not be available yet or might have issues,
# so using the standard Temurin 25 JRE image as a safer default.
FROM eclipse-temurin:25-jre

# Set the working directory
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/spring-mem-1.0-SNAPSHOT.jar app.jar

# Expose the port the app runs on (default Spring Boot port is 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
