# Use a Gradle image to build the application
FROM gradle:7.6.1-jdk17 AS build

WORKDIR /app
COPY . .

# Build the application
RUN ./gradlew build

# Use a smaller base image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/build/libs/identity-reconciliation-0.0.1-SNAPSHOT.jar /app/identity-reconciliation.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "identity-reconciliation.jar"]
