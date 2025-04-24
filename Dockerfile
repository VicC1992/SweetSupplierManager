# Use the official Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .

# Download project dependencies
RUN mvn dependency:go-offline

# Copy the whole project and build it
COPY src ./src

# Build the application and package it into a .jar file
RUN mvn clean package -DskipTests

# Now we will use a smaller image to run the app
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the built .jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]

