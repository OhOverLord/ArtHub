# 1st stage
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
COPY src /app/src
COPY config /app/config

# Copy the gradlew script
COPY gradlew /app/

# Ensure Gradle Wrapper is executable
RUN chmod +x ./gradlew

# Run Gradle build
RUN ./gradlew build --parallel --no-daemon

# 2nd stage
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar
COPY src /app/src

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

