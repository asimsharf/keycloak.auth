# Use a Java 21 base image for building the app
FROM openjdk:21-slim AS build

# Set the working directory for the build stage
WORKDIR /app

# Copy Maven files (pom.xml) to the container
COPY pom.xml /app/pom.xml

# Copy the source code to the container
COPY src /app/src

# Install Maven and dependencies, and build the app without tests
RUN apt-get update && apt-get install -y maven
RUN mvn clean install -DskipTests

# Use a smaller JRE image with Java 21 to run the app
FROM openjdk:21-slim

# Set the working directory for the runtime image
WORKDIR /app

# Copy the final built JAR file from the build image
COPY --from=build /app/target/keycloak.auth-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy the wait-for-it script and make it executable
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# Install curl for debugging
RUN apt-get update && apt-get install -y curl

# Expose the port the application will run on
EXPOSE 4000

# Command to run the Spring Boot application
CMD ["/app/wait-for-it.sh", "mysql-db:3306", "--", "java", "-jar", "/app/app.jar"]
