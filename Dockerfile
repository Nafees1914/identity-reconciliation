# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file built by Gradle into the container
COPY build/libs/identity-reconciliation-0.0.1-SNAPSHOT.jar /app/identity-reconciliation.jar

# Expose the port your Spring Boot application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/identity-reconciliation.jar"]
