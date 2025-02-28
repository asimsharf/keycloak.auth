### Dockerfile
FROM openjdk:21-slim AS build
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN apt-get update && apt-get install -y maven && mvn clean install -DskipTests

FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/target/keycloak.auth-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 4000
CMD ["java", "-jar", "/app/app.jar"]