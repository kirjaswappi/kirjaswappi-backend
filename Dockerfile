# Use the official Maven image with SAP Machine 17
FROM maven:3-sapmachine-17

# Set the working directory inside the container
WORKDIR /app

# Copy the project files (including pom.xml)
COPY . .

# Build the JAR
RUN mvn package -DskipTests

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=cloud
ENV PORT=10000

# Expose port
EXPOSE $PORT

# Run the Jar
CMD ["java", "-jar", "target/backend-1.0.0-SNAPSHOT.jar"]