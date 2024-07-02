# Use the official Maven image with SAP Machine 17
FROM maven:3-sapmachine-17

# Set the working directory inside the container
WORKDIR /app

# Copy the project's POM file
COPY pom.xml .

# Download dependencies (this step is cached if the POM hasn't changed)
RUN mvn dependency:go-offline

# Copy the all files
COPY . .

# Build the JAR
RUN mvn clean package -DskipTests

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=cloud
ENV PORT=10000

# Expose port 10000
EXPOSE 10000

# Specify the command to run your application (adjust as needed)
CMD ["java", "-jar", "target/backend-1.0.0-SNAPSHOT.jar"]
