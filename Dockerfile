# Use OpenJDK 17 as base
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/producer-kafka-1.0-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

