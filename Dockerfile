FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/Auth_microservice-0.0.1-SNAPSHOT.jar /app/auth-microservice.jar

ENTRYPOINT ["java", "-jar", "auth-microservice.jar"]